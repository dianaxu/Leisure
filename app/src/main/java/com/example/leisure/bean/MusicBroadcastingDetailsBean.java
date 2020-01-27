package com.example.leisure.bean;

import java.util.List;

public class MusicBroadcastingDetailsBean {

    /**
     * channel : 漫步春天
     * count : null
     * ch_name : public_tuijian_spring
     * artistid : null
     * avatar : null
     * songlist : [{"all_rate":"96,128,224,320,flac","charge":0,"method":0,"artist":"许绍洋","thumb":"http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_2,w_90,h_90","all_artist_id":"333","resource_type":"0","havehigh":2,"title":"花香","songid":"1123781","artist_id":"333","flow":0},{"all_rate":"64,96,128,224,320,flac","charge":0,"method":0,"artist":"任贤齐","thumb":"http://qukufile2.qianqian.com/data2/pic/642399fbc2f6e966ba5ffd9a7a580ac0/66883/66883.jpg@s_2,w_90,h_90","all_artist_id":"119","resource_type":"0","havehigh":2,"title":"春天花会开","songid":"704051","artist_id":"119","flow":0},{"all_rate":"128,flac,320,224,96","charge":0,"method":0,"artist":"易桀齐","thumb":"http://qukufile2.qianqian.com/data2/music/EF1DF29101E3BB0C09138560E2D88D38/252201586/252201586.jpg@s_2,w_90,h_90","all_artist_id":"931","resource_type":"0","havehigh":2,"title":"有你真好","songid":"696666","artist_id":"931","flow":0},{"all_rate":"128,flac,320,224,96","charge":0,"method":0,"artist":"羽泉","thumb":"http://qukufile2.qianqian.com/data2/music/5C123C4E11A70AB61A3EB2CC1E203942/252256504/252256504.jpg@s_2,w_90,h_90","all_artist_id":"786","resource_type":"0","havehigh":2,"title":"深呼吸","songid":"1159960","artist_id":"786","flow":0},{"all_rate":"128,320,flac","charge":0,"method":0,"artist":"郁可唯","thumb":"http://qukufile2.qianqian.com/data2/pic/4da2d193769c88f39c95b88b474fd1a8/578956117/578956117.jpg@s_2,w_90,h_90","all_artist_id":"1656","resource_type":"0","havehigh":2,"title":"小柠檬","songid":"630071","artist_id":"1656","flow":0},{"all_rate":"128,320,flac","charge":0,"method":0,"artist":"郁可唯","thumb":"http://qukufile2.qianqian.com/data2/pic/4da2d193769c88f39c95b88b474fd1a8/578956117/578956117.jpg@s_2,w_90,h_90","all_artist_id":"1656","resource_type":"0","havehigh":2,"title":"暖心","songid":"537345","artist_id":"1656","flow":0},{"all_rate":"96,224,128,320,flac","charge":0,"method":0,"artist":"曹格","thumb":"http://qukufile2.qianqian.com/data2/pic/4354abbe4e167688703e2da1ae5c4a2d/614313803/614313803.jpg@s_2,w_90,h_90","all_artist_id":"488","resource_type":"0","havehigh":2,"title":"吹吹风","songid":"450485","artist_id":"488","flow":0},{"all_rate":"64,96,128,224,320,flac","charge":0,"method":0,"artist":"梁静茹","thumb":"http://qukufile2.qianqian.com/data2/pic/8b9d8b1ebaeb8981213d02601fe14a11/583747637/583747637.jpg@s_2,w_90,h_90","all_artist_id":"120","resource_type":"0","havehigh":2,"title":"在晴朗的一天出发","songid":"435225","artist_id":"120","flow":0},{"all_rate":"flac,320,128,224,96","charge":0,"method":0,"artist":"易桀齐","thumb":"http://qukufile2.qianqian.com/data2/music/12D27ABDE0E75BD0C32D86AAF2C8188F/252191207/252191207.jpg@s_2,w_90,h_90","all_artist_id":"931","resource_type":"0","havehigh":2,"title":"一整片天空","songid":"1249078","artist_id":"931","flow":0},{"all_rate":"96,128,224,320,flac","charge":0,"method":0,"artist":"苏慧伦","thumb":"http://qukufile2.qianqian.com/data2/pic/70883433240509d03e93342800d86b5d/166527/166527.jpg@s_2,w_90,h_90","all_artist_id":"322","resource_type":"0","havehigh":2,"title":"Lemon Tree","songid":"1631731","artist_id":"322","flow":0},{"all_rate":"","charge":0,"method":0,"artist":null,"thumb":"","all_artist_id":null,"resource_type":null,"havehigh":0,"title":null,"songid":null,"artist_id":null,"flow":0}]
     * channelid : null
     */

    public String channel;
    public Object count;
    public String ch_name;
    public Object artistid;
    public Object avatar;
    public Object channelid;
    public List<SonglistBean> songlist;



    public static class SonglistBean {
        /**
         * all_rate : 96,128,224,320,flac
         * charge : 0
         * method : 0
         * artist : 许绍洋
         * thumb : http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_2,w_90,h_90
         * all_artist_id : 333
         * resource_type : 0
         * havehigh : 2
         * title : 花香
         * songid : 1123781
         * artist_id : 333
         * flow : 0
         */

        public String all_rate;
        public int charge;
        public int method;
        public String artist;
        public String thumb;
        public String all_artist_id;
        public String resource_type;
        public int havehigh;
        public String title;
        public String songid;
        public String artist_id;
        public int flow;
    }
}
