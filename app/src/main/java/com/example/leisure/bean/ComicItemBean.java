package com.example.leisure.bean;

import com.example.leisure.retrofit.BaseComicResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ComicItemBean extends BaseComicResponse implements Serializable {
    /**
     * code : 0
     * message : 成功!
     * data : {"time":"2019-12-31","introduce":"一场突如其来的的灾难降临，到底是有预谋，还是被殃及无辜？阴谋和阳谋的交织，迷雾重重\u2026\u2026\u2026男主的身世逐渐揭开帷幕，他想要成为强者，付出代价，忍常人所不能忍，逆袭之路就此开始。看废柴少年如何撬动世界，改编自小说《古尊》","name":"绝世古尊","cover":"https://img.detatu.com/upload/vod/2019-09-17/15686950561.jpg","author":"阿柯文化","tag":"少年热血","latest":"第46话天绝杀手"}
     * list : [{"num":"预告","url":"mh123/comic/26620/1421232.html"},{"num":"第1话吃鸡少年石弋轩","url":"mh123/comic/26620/1421233.html"},{"num":"第2话你要对我干什么！","url":"mh123/comic/26620/1421234.html"},{"num":"第3话男人的嘴骗人鬼","url":"mh123/comic/26620/1421235.html"},{"num":"第4话突如其来的灾难","url":"mh123/comic/26620/1421236.html"},{"num":"第5话另一处战场","url":"mh123/comic/26620/1421237.html"},{"num":"第6话把传承交出来！","url":"mh123/comic/26620/1421238.html"},{"num":"第7话光翼展开！","url":"mh123/comic/26620/1421239.html"},{"num":"第8话袭击风无烟！","url":"mh123/comic/26620/1421240.html"},{"num":"第9话教练！我想学功法","url":"mh123/comic/26620/1421241.html"},{"num":"第10话无处安放的OO","url":"mh123/comic/26620/1421242.html"},{"num":"第11话前方妖兽出没！","url":"mh123/comic/26620/1421243.html"},{"num":"第12话让你尝尝我的厉害","url":"mh123/comic/26620/1421244.html"},{"num":"第13话破碎的天空","url":"mh123/comic/26620/1421245.html"},{"num":"第14话你对力量一无所知！","url":"mh123/comic/26620/1421246.html"},{"num":"第15话毫无生还机会","url":"mh123/comic/26620/1421247.html"},{"num":"第16话与我一战三生有幸","url":"mh123/comic/26620/1427457.html"},{"num":"第17话新的旅途新的开始","url":"mh123/comic/26620/1431071.html"},{"num":"第18话初次相遇","url":"mh123/comic/26620/1435295.html"},{"num":"第19话遇袭","url":"mh123/comic/26620/1439991.html"},{"num":"第20话天玄气旋斩","url":"mh123/comic/26620/1441418.html"},{"num":"第21话修真者的战斗","url":"mh123/comic/26620/1442526.html"},{"num":"第22话我放手！","url":"mh123/comic/26620/1442527.html"},{"num":"第23话前辈带带我！","url":"mh123/comic/26620/1445916.html"},{"num":"第24话天罗道院","url":"mh123/comic/26620/1450090.html"},{"num":"第25话秘技反复横跳","url":"mh123/comic/26620/1454319.html"},{"num":"第26话吃我一JIO","url":"mh123/comic/26620/1459963.html"},{"num":"第27话只有一张床","url":"mh123/comic/26620/1462305.html"},{"num":"第28话好久不见","url":"mh123/comic/26620/1466841.html"},{"num":"第29话把人放下！","url":"mh123/comic/26620/1469676.html"},{"num":"第30话万兽千魔掌","url":"mh123/comic/26620/1472561.html"},{"num":"第31话哥打的就是精锐","url":"mh123/comic/26620/1476042.html"},{"num":"第32话我什么都没看到！","url":"mh123/comic/26620/1481626.html"},{"num":"第33话入院考核","url":"mh123/comic/26620/1487408.html"},{"num":"第34话天雷！落！","url":"mh123/comic/26620/1491349.html"},{"num":"第35话救救我！","url":"mh123/comic/26620/1496275.html"},{"num":"第36话空手碎白刃！","url":"mh123/comic/26620/1499173.html"},{"num":"第37话天谴之子","url":"mh123/comic/26620/1501226.html"},{"num":"第38话聚灵阵","url":"mh123/comic/26620/1504191.html"},{"num":"第39话诶？诶！诶！！！","url":"mh123/comic/26620/1507328.html"},{"num":"第40话螳螂捕蝉黄雀在后","url":"mh123/comic/26620/1511104.html"},{"num":"第41话神秘黑衣人","url":"mh123/comic/26620/1513622.html"},{"num":"第42话鬼打墙","url":"mh123/comic/26620/1518333.html"},{"num":"第43话迷雾间的小屋","url":"mh123/comic/26620/1520743.html"},{"num":"第44话你..你们在做什么！","url":"mh123/comic/26620/1524618.html"},{"num":"第45话死灵来袭","url":"mh123/comic/26620/1527497.html"},{"num":"第46话天绝杀手","url":"mh123/comic/26620/1530355.html"}]
     */

    public ComicItemBean.ComicDataBean data;
    public List<ComicItemBean.ChapterBean> list;

    public static class ComicDataBean {
        /**
         * time : 2019-12-31
         * introduce : 一场突如其来的的灾难降临，到底是有预谋，还是被殃及无辜？阴谋和阳谋的交织，迷雾重重………男主的身世逐渐揭开帷幕，他想要成为强者，付出代价，忍常人所不能忍，逆袭之路就此开始。看废柴少年如何撬动世界，改编自小说《古尊》
         * name : 绝世古尊
         * cover : https://img.detatu.com/upload/vod/2019-09-17/15686950561.jpg
         * author : 阿柯文化
         * tag : 少年热血
         * latest : 第46话天绝杀手
         */

        public String time;
        public String introduce;
        public String name;
        public String cover;
        public String author;
        public String tag;
        public String latest;
    }

    public static class ChapterBean implements Serializable {
        /**
         * num : 预告
         * url : mh123/comic/26620/1421232.html
         */

        public String num;
        public String url;
        public boolean visible;
        public boolean isReading;

        public List<ComicContentBean.ListBean> list = new ArrayList<>();
    }
}
