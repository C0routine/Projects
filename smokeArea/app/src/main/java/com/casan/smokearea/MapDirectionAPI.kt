package com.casan.smokearea

import android.os.Parcelable
import retrofit2.http.GET
import com.google.gson.annotations.SerializedName
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Query

// GET 요청
// https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving
// start=문자열 들어감&goal=문자열 들어감&option=trafast(옵션방식)
interface MapDirectionAPI {
    @GET("map-direction/v1/driving")
    fun getDirection(
        @Query("start") start:String,
        @Query("goal") goal:String,
        @Query("option") option:String, // trafast 실시간 빠른길로

        @Header("X-NCP-APIGW-API-KEY-ID") keyId:String,
        @Header("X-NCP-APIGW-API-KEY") key:String
    ): Call<MapDirections>
}

// GET 의 Request 하면서 json 파일에서 다음과 같은 이름을 가지고 있으면은 변수에 저장.
@Parcelize // 직렬화
data class MapDirections(
    // 길찾기 성공여부
    @SerializedName("code") val code:Int,

    // 시작 지점 위치값
    @SerializedName("start") val start:LatLng,
    // 도착 지점 위치값
    @SerializedName("goal") val goal:LatLng,

    // 전체 경로 거리 (meters)
    @SerializedName("distance") val distance:Int,

    // 전체 경로 소요 시간 (millisecond 단위)
    // 차로 가는 기준이기 때문에 사람의 속도로 재연산 필요
    @SerializedName("duration") val duration:Int,

    // 경로를 구성하는 모든 좌표열
    @SerializedName("duration") val path:List<LatLng>
):Parcelable
