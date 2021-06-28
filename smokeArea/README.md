# SmokeArea Project

## 목차
- 사용된 기술 스택
- 앱 개요
- 앱 구동 화면
- 업데이트 방향성 
- 기타
<br>
<br>

### 사용된 기술
> Room
> ListAdapter + RecyclerView
> NaverMap Open API
> Url Scheme
> 지도 좌표 찾기

<br>


### 앱 개요
    비흡연 구역에서 흡연 하는 흡연자를 자주 보게 되었고, 흡연자 분들이 흡연할 구역을 
    모르기에 이를 안내하기 위하여 제작하게 되었습니다. 
    
    모든 안내 지역은 정부 공식아래의 흡연구역으로 지정된곳만 안내합니다. 
    (현재는 서초구 지역만 설정 되어 있습니다)
    
    흡연자가 담배를 구매하였을 경우 구매 하였음을 입력해주면은 입력한 시간을 기준으로 
    시간과 금액이 자동으로 DB 에 저장되어 한꺼번에 조회 할 수 있습니다.
    
<br>

### 앱 구동 화면 

![enter image description here](https://github.com/C0routine/Projects/blob/main/smokeArea/smokeArea01.png)
<br>
![enter image description here](https://github.com/C0routine/Projects/blob/main/smokeArea/smokeArea02.png)
<br>
![enter image description here](https://github.com/C0routine/Projects/blob/main/smokeArea/smokeArea03.png)
<br>
![enter image description here](https://github.com/C0routine/Projects/blob/main/smokeArea/smokeArea04.png)


<br><br>

### 업데이트 방향성

    다른 지역의 흡연구역 데이터를 추가.
    담배 구매시 금액 직접입력으로 수정.
    담배 구매 잘못 입력시 삭제 기능 제공
    금연 관련 기능 제공

<br>

### 기타 

    데이터를 제공을 하는 서버와 통신을 위하여 Retrofit 을 이용할려 하였지만 
    해당 Open API 제공 기관에서 하루 최대 1천 건의 제한과 
    명확하지 않은 위치의 데이터를 제공하기에 이를 폐기하고, 
    코드에 좌표를 일일이 찾아서 입력해주게 되었습니다.
    
    자세한 코드 해석은 주석처리로 정리하였으니 참고 하시면 됩니다.


 
