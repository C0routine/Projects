package com.casan.smokearea

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.casan.smokearea.databinding.ActivityMainBinding
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var bind: ActivityMainBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // action bar(title bar) hide
        supportActionBar?.hide()

        // mapFragment (map ui 권장방법으로 load)
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        // 비동기로 NaverMap 객체 준비, onMapReady callback method call
        mapFragment.getMapAsync(this)
        // map 을 사용하기 위해 필요한 위치와 관련된 값들과 권한 요청
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // popup menu 관련
        popUpMenuEvent()
    }

    @SuppressLint("SimpleDateFormat")
    private fun popUpMenuEvent(){
        // db load
        val db = AppDatabase.getInstance(applicationContext)

        // popup menu listener, popup show
        bind.menuButton.setOnClickListener {
            // popupmenu load 할 layout selected (popup menu create)
            val popupMenu = PopupMenu(this, bind.menuButton)
            // create 한 popupmenu 형태의 layout 을 지정
            menuInflater.inflate(R.menu.popup, popupMenu.menu)

            // sharedPreferences 객체 생성
            val money = getSharedPreferences("sMoney", MODE_PRIVATE)

            // shared 에 usemoney 로 저장된 data load 없을 시 0
            popupMenu.menu.getItem(0).title = "사용 금액 : ${money.getInt("usemoney", 0)}"

            // popup menu 의 itme click event 발생시 수행할 작업
            popupMenu.setOnMenuItemClickListener{
                when(it.itemId){

                    R.id.PopUpitem_money -> {
                        AlertDialog.Builder(this).apply{
                            setMessage("담배를 구매하셨나요?")
                            setPositiveButton("Yes") { _, _ ->
                                money.edit().apply {
                                    // yes 누를시 data 값 가져와서 +5000 하여 재 저장
                                    putInt("usemoney", money.getInt("usemoney", 0)+5000)
                                    apply()
                                }

                                val simpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일 EE요일 HH:mm")
                                val dateInfo = simpleDateFormat.format(Calendar.getInstance().time)

                                thread {
                                    db!!.userDao().insertTime(UserTimeTable(null, dateInfo, money.getInt
                                        ("usemoney", 0)))
                                }
                            }
                            // no 누를시 종료
                            setNegativeButton("No"){_,_->
                                return@setNegativeButton
                            }
                        }.create().show()
                    }

                    // 자세히 보기 부분
                    R.id.PopUpitem_allShow -> {
                        val intent = Intent(this, UserSmoke::class.java)
                        startActivity(intent)
                    }
                }
                return@setOnMenuItemClickListener false
            }
            popupMenu.show()
        }
    }

    // location permission 요청시 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // permission denied 거부
                // 권한 없을시 위치를 추적하지 않음
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naver: NaverMap) {
        naverMap = naver // naverMap 초기화
        // location source 지정, 위치추적 기능 지금부터 사용가능 (map 과 관련된 기능 모두 사용가능)
        naverMap.locationSource = locationSource
        naverMap.minZoom = 6.0 // 최대 축소값 지정 (최대 한국까지만 보이도록 설정)

        marker = Marker() // marker 객체 생성
        val infoWindow = InfoWindow() // 정보창 객체 생성

        // uisetting 은 활성화 여부만 제어 가능
        // User Interface load
        val uiSettings = naverMap.uiSettings

        // map controller hide (기본 줌 숨김처리, 제스처 컨트롤은 가능)
        // 나침반 (compass) 숨김 처리 layout 배정을 위해서 따로 관리
        uiSettings.isZoomControlEnabled = false
        uiSettings.isCompassEnabled = false

        // location button 활성화 (layout button)
        bind.mapLocationButton.map = naverMap
        // compass View 활성화 (layout view)
        bind.compassView.map = naverMap

        // main thread handler get
        val handler = Handler(Looper.getMainLooper())
        // background Thread Start
        thread {
            /*
            대량의 오버레이를 다룰 경우 객체를 생성하고 초기 옵션을 지정하는 작업은
            백그라운드 스레드에서 수행하고 지도에 추가하는 작업만을 메인 스레드에서
            수행하면 메인 스레드를 효율적으로 사용할 수 있습니다.
             */

            // 백그라운드 스레드로 객체를 생성하고 데이터 자료 로드
            val markers = SmokingMarkers().markerAllInfo()
            // 여러개의 마커 색상이 바뀌는것을 방지하기 위한 변수
            var markerColorRed = false

            markers.forEach{ marker->

                // 각 마커에 클릭 리스너 부여
                marker.setOnClickListener {
                    infoWindow.open(marker) // 마커 정보창을 열음

                    // 각 overlay 에 click Listener 부여 (정보창에 클릭 리스너 부여)
                    infoWindow.onClickListener = Overlay.OnClickListener {
                        // 해당 마커의 좌표값
                        Log.d("cycle", "${marker.position}")

                        // 현재 사용자 위치값 저장
                        val realLatitude = locationSource.lastLocation?.latitude
                        val realLongitude = locationSource.lastLocation?.longitude

                        // 위치 서비스가 켜져있는지 꺼져있는지 확인
                        // overlay(정보창)가 켜져있을때만 클릭 할 수 있음
                        if(realLatitude != null && realLongitude != null){
                            // 위치서비스가 켜져있을 경우

                            try { // naver map 지도 어플이 있을경우 실행
                                startActivity(Intent(Intent.ACTION_VIEW,
                                    Uri.parse(url
                                            + "slat=${realLatitude}" // 사용자 위치
                                            +"&slng=${realLongitude}" // 사용자 위치
                                            +"&dlat=${marker.position.latitude}" // 목표 위치
                                            +"&dlng=${marker.position.longitude}" // 목표 위치
                                            +"&dname=${marker.tag}" // 목표지점 임시 텍스트
                                    )))
                            } catch (anfe:android.content.ActivityNotFoundException) {
                                // 없을경우 naver map 지도 설치를 위해 플레이스토어로 이동
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(naverUrl)))
                                Toast.makeText(this,"길찾기를 위해 네이버 지도를 설치해주세요"
                                    , Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            // 위치 서비스가 꺼져있을 경우
                            Toast.makeText(this,
                                "위치 서비스를 켜주세요", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }

                    // 각 마커의 색상설정과 카메라 뷰 업데이트
                    // 해당 marker 처음클릭할시 색상 빨강으로 (다른 마커가 눌려있지 않을경우)
                    if (marker.icon == MarkerIcons.GREEN && !markerColorRed) {
                        marker.icon = MarkerIcons.RED
                        markerColorRed = true

                        // cameraUpdate 객체를 생성하여 camera 를 지정한 오버레이 좌표로 확대해서 변경,
                        // camera 변경 과정 애니메이션 적용, 지속시간 1.5초
                        val cameraUpdate = CameraUpdate.scrollAndZoomTo(marker.position, 16.0)
                            .animate(CameraAnimation.Easing, 1500)
                        naverMap.moveCamera(cameraUpdate) // camera update
                    }
                    // 이미 클릭했던 것을 재 클릭할 경우, 정보창 닫고 색상 원래대로
                    else if(marker.icon == MarkerIcons.RED && markerColorRed){
                        infoWindow.close()
                        marker.icon = MarkerIcons.GREEN
                        markerColorRed = false
                    }
                    // 다른곳 마커가 이미 선택되어 있는데 선택할 경우
                    else{
                        // 모든 마커 기존색으로 전환
                        markers.forEach {
                            it.icon = MarkerIcons.GREEN
                        }
                        // 누른 마커만 다시 색상 변경
                        marker.icon = MarkerIcons.RED
                        // cameraUpdate 객체를 생성하여 camera 를 지정한 오버레이 좌표로 확대해서 변경,
                        // camera 변경 과정 애니메이션 적용, 지속시간 1.5초
                        val cameraUpdate = CameraUpdate.scrollAndZoomTo(marker.position, 16.0)
                            .animate(CameraAnimation.Easing, 1500)
                        naverMap.moveCamera(cameraUpdate) // camera update
                    }
                    true
                }

                // 각 오버레이 정보창에 열리는 것을 custom 한 레이아웃 형식으로 변환하고 데이터를 넣어줌
                infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(this) {
                    // custom item 정보 표현
                    @SuppressLint("SetTextI18n") // 경고 처리 없앰.
                    override fun getContentView(infowindow: InfoWindow): View {
                        val view = View.inflate(this@MainActivity, R.layout.overlay_item, null)
                        // 기존 data 에 저장된 값들을 불러와서 각 레이아웃에 넣어줌
                        view.findViewById<TextView>(R.id.addressTextView).text = infowindow.marker?.tag.toString()
                        view.findViewById<TextView>(R.id.areaTypeTextView).text = infowindow.marker?.subCaptionText
                        return view
                    }
                }

            }

            // 오버레이드 객체 멀티스레딩 (오버레이 여러개 표시 가능하게끔)
            handler.post {
                // main thread 부분
                markers.forEach { marker ->
                    marker.map = naverMap // 오버레이(마커)를 지도에 추가
                    //marker.icon = OverlayImage.fromResource(R.drawable.ic_smoke) // icon custom
                }
            }
        }

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val naverUrl = "market://details?id=com.nhn.android.nmap"
        private const val url = "nmap://route/walk?"

        /* 기능 필요 없어서 주석
        data 를 요청하여 json을 받아오는 형식으로 할려 했지만
        드라이빙 open api 만 제공이 되어서 도보 제공을 위하여 retrofit2
        방식을 쓰지 않기로 함.

        private const val keyId = ""
        private const val key = ""
        private const val option = "trafast"

        private val retrofit = Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service:MapDirectionAPI = retrofit.create(MapDirectionAPI::class.java)
         */
    }
}

