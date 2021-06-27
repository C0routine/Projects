package com.casan.smokearea

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker

class SmokingMarkers{
    // address 개수와 facility 개수와 좌표 개수가 맞지 않으면 예외 발생
    class UnvalidSizeAddressFacility(msg:String):Exception(msg)

    // 모든 marker 가 있을 변수
    private val markers = mutableListOf<Marker>()

    fun markerAllInfo():List<Marker>{
        Seoul().seocho() // 서울시 서초구 지역을 markers 에 추가

        return markers // save 된 data return
    }

    // data input 편의성을 위해서 data 를 listOf 로 선언
    // 서울시 흡연시설
    inner class Seoul{

        // 서초구
        fun seocho(){
            // 서초구 흡연시설 주소
            val seochoAddress = listOf<String>(
                "사당역 2번 출구와 3번 출구 사이 보도",
                "강남역 7번 출구와 8번출구 사이 보도",
                "방배역3번출구 먹자골목 입구",
                "방배동 450-14 앞",
                "서초대로 78길 24 스타벅스 앞",
                "서초대로 78길 42 현대기림오피스텔 앞",
                "원지동 23 서초종합체육센터 입구",
                "서울시공공자전거 수서센터 옆",
                "방배동 455-10 앞(방배경찰서 앞)",
                "방배동 452-1 앞 (그린골프장 주차장 출구 앞)",
                "서초동 1366-4 서희타워 옆 골목",
                "양재환승주차장 세븐일레븐 편의점 옆",
                "서초구청 주차장 자판기 옆",
                "교보생명보험 서초사옥 주차장 출구 맞은편",
                "엘타워 뒤 주차장 진입로 옆",
                "서울가정행정법원 청사 출입계단 밑",
                "센트럴시티 경부선 하차장 옆",
                "센트럴시티 호남선 출입구 옆",
                "센트럴시티  경부선 건물 남쪽 외부 공간",
                "고속버스터미널역 3번출구 앞"
            )

            // 서초구 흡연시설 시설형태
            val seochoFacility = listOf<String>(
                "개방형흡연부스",
                "개방형흡연부스",
                "개방형흡연부스",
                "개방형흡연부스",
                "개방형흡연부스",
                "개방형흡연부스",
                "개방형흡연부스",
                "개방형흡연부스",
                "개방형흡연부스",
                "개방형흡연부스",
                "라인형흡연구역",
                "라인형흡연구역",
                "폐쇄형흡연부스",
                "개방형흡연구역",
                "개방형흡연구역",
                "부분개방형흡연실",
                "부분개방형흡연부스",
                "부분개방형흡연부스",
                "개방형흡연구역",
                "부분개방형흡연구역"
            )

            // 서초구 흡연시설 좌표 값
            val seochoLatLng = listOf<LatLng>(
                LatLng(37.47565953988722, 126.98194070439669),
                LatLng(37.49744439030157, 127.027418306858),
                LatLng(37.48199757341713, 126.99712451990177),
                LatLng(37.47768492795913, 126.98267138157053),
                LatLng(37.49550719956195, 127.02789086082072),
                LatLng(37.494004507153555, 127.02859651839798),
                LatLng(37.459198891972235, 127.04128348200068),
                LatLng(37.48396272789485, 126.98243633721336),
                LatLng(37.48154051236883, 126.98245614658693),
                LatLng(37.478975692082315, 126.9824125735873),
                LatLng(37.48453234989907, 127.03284099878714),
                LatLng(37.483479622057004, 127.03401488005349),
                LatLng(37.48389970484184, 127.03208617373888),
                LatLng(37.50335405267748, 127.02381382978841),
                LatLng(37.48193078130927, 127.03546258065228),
                LatLng(37.481279046725916, 127.03637915593104),
                LatLng(37.506221067064516, 127.00654721580086),
                LatLng(37.50506334038378, 127.00324867861517),
                LatLng(37.504189463231256, 127.00558704826067),
                LatLng(37.50350136123337, 127.00561611104901),
            )

            // 서초구 흡연시설 과 주소록, 좌표 값의 개수가 같을 경우만 배치
            if (seochoAddress.size == seochoFacility.size && seochoLatLng.size == seochoFacility.size) {
                for(loop in seochoAddress.indices){
                    markers.add(Marker().apply {
                        position = seochoLatLng[loop] // 좌표값 저장
                        // captionText = seochoAddress[loop]
                        // captionText 로 하면은 마커 밑단에도 표시가 됨
                        subCaptionText = seochoFacility[loop] // 시설형태 저장
                        tag = seochoAddress[loop] // 주소 저장
                    })
                }
            } else throw UnvalidSizeAddressFacility("Seocho Error")
        }

    }
}
