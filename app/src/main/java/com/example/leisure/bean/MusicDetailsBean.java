package com.example.leisure.bean;

import java.util.List;

public class MusicDetailsBean {

    /**
     * xcode : 8681018db5c74f7ac938e504f7d93823
     * songList : [{"songName":"在晴朗的一天出发","albumName":"闪亮的星","linkCode":22000,"format":"mp3","albumId":72071,"artistId":"1095","source":"web","songPicBig":"http://qukufile2.qianqian.com/data2/pic/8b9d8b1ebaeb8981213d02601fe14a11/583747637/583747637.jpg@s_2,w_150,h_150","version":"","queryId":"435225","songLink":"http://audio04.dmhmusic.com/71_53_T10041165821_128_4_1_0_sdk-cpm/cn/0208/M00/31/84/ChR461pkYjuALHeRAEEW2lcE6oY561.mp3?xcode=dc30e4fcd4fa13a85984d4ccfabec839c0ec7be","size":4265690,"rate":128,"lrcLink":"http://qukufile2.qianqian.com/data2/lrc/28744935/28744935.lrc","copyType":0,"artistName":"梁静茹","time":266,"relateStatus":"0","songPicSmall":"http://qukufile2.qianqian.com/data2/pic/8b9d8b1ebaeb8981213d02601fe14a11/583747637/583747637.jpg@s_2,w_90,h_90","songId":435225,"songPicRadio":"http://qukufile2.qianqian.com/data2/pic/8b9d8b1ebaeb8981213d02601fe14a11/583747637/583747637.jpg@s_2,w_300,h_300","showLink":"http://audio04.dmhmusic.com/71_53_T10041165821_128_4_1_0_sdk-cpm/cn/0208/M00/31/84/ChR461pkYjuALHeRAEEW2lcE6oY561.mp3?xcode=dc30e4fcd4fa13a85984d4ccfabec839c0ec7be","resourceType":"0"}]
     */

    public String xcode;
    public List<SongListBean> songList;

    public static class SongListBean {
        /**
         * songName : 在晴朗的一天出发
         * albumName : 闪亮的星
         * linkCode : 22000
         * format : mp3
         * albumId : 72071
         * artistId : 1095
         * source : web
         * songPicBig : http://qukufile2.qianqian.com/data2/pic/8b9d8b1ebaeb8981213d02601fe14a11/583747637/583747637.jpg@s_2,w_150,h_150
         * version : 
         * queryId : 435225
         * songLink : http://audio04.dmhmusic.com/71_53_T10041165821_128_4_1_0_sdk-cpm/cn/0208/M00/31/84/ChR461pkYjuALHeRAEEW2lcE6oY561.mp3?xcode=dc30e4fcd4fa13a85984d4ccfabec839c0ec7be
         * size : 4265690
         * rate : 128
         * lrcLink : http://qukufile2.qianqian.com/data2/lrc/28744935/28744935.lrc
         * copyType : 0
         * artistName : 梁静茹
         * time : 266
         * relateStatus : 0
         * songPicSmall : http://qukufile2.qianqian.com/data2/pic/8b9d8b1ebaeb8981213d02601fe14a11/583747637/583747637.jpg@s_2,w_90,h_90
         * songId : 435225
         * songPicRadio : http://qukufile2.qianqian.com/data2/pic/8b9d8b1ebaeb8981213d02601fe14a11/583747637/583747637.jpg@s_2,w_300,h_300
         * showLink : http://audio04.dmhmusic.com/71_53_T10041165821_128_4_1_0_sdk-cpm/cn/0208/M00/31/84/ChR461pkYjuALHeRAEEW2lcE6oY561.mp3?xcode=dc30e4fcd4fa13a85984d4ccfabec839c0ec7be
         * resourceType : 0
         */

        public String songName;
        public String albumName;
        public int linkCode;
        public String format;
        public int albumId;
        public String artistId;
        public String source;
        public String songPicBig;
        public String version;
        public String queryId;
        public String songLink;
        public int size;
        public int rate;
        public String lrcLink;
        public int copyType;
        public String artistName;
        public int time;
        public String relateStatus;
        public String songPicSmall;
        public int songId;
        public String songPicRadio;
        public String showLink;
        public String resourceType;

    }
}
