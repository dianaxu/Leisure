package com.example.leisure.db.greendao;

import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.greenDao.gen.TestBeanDao;
import com.example.leisure.greenDao.gen.TestListBeanDao;
import com.example.leisure.retrofit.BaseComicResponse;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity
public class TestBean extends BaseComicResponse {

    /**
     * code : 0
     * message : 成功!
     * list : [{"name":"绝世古尊","url":"mh123/comic/26620.html","cover":"https://img.detatu.com/upload/vod/2019-09-17/15686950561.jpg","time":"2020-01-05","latest":"第47话大..大蒜！"},{"name":"烈火女将","url":"mh123/comic/28549.html","cover":"https://img.detatu.com/upload/pic/2019-11-07/15730991360.jpg","time":"2020-01-05","latest":"第75话是，我杀了她"},{"name":"火锅家族第二季","url":"mh123/comic/24866.html","cover":"https://img.detatu.com/upload/vod/2019-05-02/15567810660.jpg","time":"2020-01-05","latest":"推陈出新"},{"name":"厄运之王","url":"mh123/comic/12193.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/15467924755.jpg","time":"2020-01-05","latest":"149 149"},{"name":"神之一脚","url":"mh123/comic/20699.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/15467940732.jpg","time":"2020-01-05","latest":"第二百一十七波 无影"},{"name":"总裁大叔不可以","url":"mh123/comic/17977.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/154679325819.jpg","time":"2020-01-05","latest":"148 尾声"},{"name":"龙王妃子不好当","url":"mh123/comic/22688.html","cover":"https://img.detatu.com/upload/vod/2019-03-23/15533316630.jpg","time":"2020-01-05","latest":"第92话区区小蛊也想奈何我？"},{"name":"被废弃的皇妃","url":"mh123/comic/20896.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/15467941232.jpg","time":"2020-01-05","latest":"他国追杀"},{"name":"重生之影后养成计划","url":"mh123/comic/24094.html","cover":"https://img.detatu.com/upload/vod/2019-03-22/15532332301.jpg","time":"2020-01-05","latest":"第26话"},{"name":"大兴国记之假凤虚凰","url":"mh123/comic/12046.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/154679244317.jpg","time":"2020-01-05","latest":"58 尾声二"},{"name":"旷野之境","url":"mh123/comic/20697.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/15467938756.jpg","time":"2020-01-05","latest":"第四十八话 真相"},{"name":"在那里工作的结小姐","url":"mh123/comic/21212.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/15467942036.jpg","time":"2020-01-05","latest":"第26话"},{"name":"师父，那个很好吃","url":"mh123/comic/25447.html","cover":"https://img.detatu.com/upload/vod/2019-07-01/15619126282.jpg","time":"2020-01-05","latest":"第62话天魔惊现！疑云重重"},{"name":"废柴重生之我要当大佬","url":"mh123/comic/29233.html","cover":"https://img.detatu.com/upload/pic/2019-12-28/15774744444.jpg","time":"2020-01-05","latest":"29.收买商会"},{"name":"拥有开挂技能「薄影」的公会职员原来是传说级别的暗杀者","url":"mh123/comic/26121.html","cover":"https://img.detatu.com/upload/vod/2019-08-02/15647435940.jpg","time":"2020-01-05","latest":"第06话"},{"name":"超能同学","url":"mh123/comic/20702.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/15467940735.jpg","time":"2020-01-05","latest":"15 妈妈的玉佩"},{"name":"蓁仙记","url":"mh123/comic/22961.html","cover":"https://img.detatu.com/upload/vod/2019-01-11/201901111547137135.jpg","time":"2020-01-05","latest":"第55话"},{"name":"娘子嫁到","url":"mh123/comic/21621.html","cover":"https://img.detatu.com/upload/vod/2019-09-14/15683963732.jpg","time":"2020-01-05","latest":"087伶牙俐齿的薛谊"},{"name":"将军请出征","url":"mh123/comic/22830.html","cover":"https://img.detatu.com/upload/vod/2019-01-08/201901081546940798.jpg","time":"2020-01-05","latest":"不管你了"},{"name":"我的英雄学园","url":"mh123/comic/20254.html","cover":"https://img.detatu.com/upload/pic/2019-12-17/15765628071.jpg","time":"2020-01-05","latest":"第256话 天高云淡"},{"name":"这个猫妖不好惹","url":"mh123/comic/28228.html","cover":"https://img.detatu.com/upload/pic/2019-10-18/15713905602.jpg","time":"2020-01-05","latest":"第166话"},{"name":"十二少女星·川溪入梦","url":"mh123/comic/19409.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/154679369311.jpg","time":"2020-01-05","latest":"告白"},{"name":"南风过境","url":"mh123/comic/24917.html","cover":"https://img.detatu.com/upload/vod/2019-05-10/15574844666.jpg","time":"2020-01-05","latest":"第三十话"},{"name":"都市超级神尊","url":"mh123/comic/28108.html","cover":"https://img.detatu.com/upload/pic/2019-12-09/15758804123.jpg","time":"2020-01-05","latest":"第28话 老婆最大"},{"name":"乱世神罚：武王大人请入戏","url":"mh123/comic/24093.html","cover":"https://img.detatu.com/upload/vod/2019-10-13/15709541430.jpg","time":"2020-01-05","latest":"第130话 一起开创新天地"},{"name":"行走阴阳","url":"mh123/comic/25638.html","cover":"https://img.detatu.com/upload/pic/2019-10-27/15721594620.jpg","time":"2020-01-05","latest":"沼林遇险"},{"name":"天工谱","url":"mh123/comic/29187.html","cover":"https://img.detatu.com/upload/pic/2019-12-20/157677713011.jpg","time":"2020-01-05","latest":"第五话 斩裂黑暗"},{"name":"诛邪","url":"mh123/comic/28224.html","cover":"https://img.detatu.com/upload/pic/2019-10-18/15713343973.jpg","time":"2020-01-05","latest":"不请自来"},{"name":"男神爸比快到碗里来","url":"mh123/comic/29281.html","cover":"https://img.detatu.com/upload/pic/2019-12-28/157747445217.jpg","time":"2020-01-05","latest":"幸福时光"},{"name":"天降萌妻","url":"mh123/comic/29278.html","cover":"https://img.detatu.com/upload/pic/2019-12-28/157747445014.jpg","time":"2020-01-05","latest":"欲言又止"},{"name":"恶饿鬼报告漫画","url":"mh123/comic/28322.html","cover":"https://img.detatu.com/upload/pic/2019-10-22/15717331343.jpg","time":"2020-01-05","latest":"第40话"},{"name":"梦魇总裁的专属甜点","url":"mh123/comic/17918.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/154679324318.jpg","time":"2020-01-05","latest":"番外18免费福利"},{"name":"神武天尊","url":"mh123/comic/25704.html","cover":"https://img.detatu.com/upload/vod/2019-07-19/15635232131.jpg","time":"2020-01-05","latest":"第73话天元夜市，三眼纹狼"},{"name":"亿万富婆在冷宫","url":"mh123/comic/28233.html","cover":"https://img.detatu.com/upload/pic/2019-12-03/15753496410.jpg","time":"2020-01-05","latest":"第13话皇帝要捆绑？王爷要亲亲？！"},{"name":"给我闭嘴！","url":"mh123/comic/25429.html","cover":"https://img.detatu.com/upload/vod/2019-06-28/15617143794.jpg","time":"2020-01-05","latest":"第31话1走私犯"},{"name":"我家驸马竟要和我炒CP","url":"mh123/comic/27747.html","cover":"https://img.detatu.com/upload/vod/2019-09-28/15696633581.jpg","time":"2020-01-05","latest":"第27话你不拦我？！"},{"name":"降妖贱师","url":"mh123/comic/19429.html","cover":"https://img.detatu.com/upload/vod/2019-03-22/15532477547.jpg","time":"2020-01-05","latest":"第142话"},{"name":"逆天神医","url":"mh123/comic/23734.html","cover":"https://img.detatu.com/upload/vod/2019-03-21/15531473350.jpg","time":"2020-01-05","latest":"第73话"},{"name":"万妖王","url":"mh123/comic/24597.html","cover":"https://img.detatu.com/upload/vod/2019-06-09/15600551040.jpg","time":"2020-01-05","latest":"第四十回爆"},{"name":"他和她的魔法契约","url":"mh123/comic/28775.html","cover":"https://img.detatu.com/upload/pic/2019-12-28/15774744422.jpg","time":"2020-01-05","latest":"第14话暗流"},{"name":"虹猫蓝兔大话成语","url":"mh123/comic/25032.html","cover":"https://img.detatu.com/upload/vod/2019-05-24/15586891550.jpg","time":"2020-01-05","latest":"第165话奉公守法"},{"name":"魔世西游","url":"mh123/comic/28103.html","cover":"https://img.detatu.com/upload/vod/2019-10-13/15709541723.jpg","time":"2020-01-05","latest":"第14话旧识"},{"name":"乱世帅府：听说司佑良爱我很多年","url":"mh123/comic/28517.html","cover":"https://img.detatu.com/upload/pic/2019-11-03/157275606014.jpg","time":"2020-01-05","latest":"第57话又如妖妇人，绸缪蛊其夫"},{"name":"女神的布衣兵王","url":"mh123/comic/28493.html","cover":"https://img.detatu.com/upload/pic/2019-11-01/15726004240.jpg","time":"2020-01-05","latest":"第20话我是红叶的未婚夫"},{"name":"厨厨动人","url":"mh123/comic/24738.html","cover":"https://img.detatu.com/upload/vod/2019-08-06/15650807120.jpg","time":"2020-01-05","latest":"第56话芙蓉酥"},{"name":"一等家丁","url":"mh123/comic/28630.html","cover":"https://img.detatu.com/upload/pic/2019-11-15/15737955853.jpg","time":"2020-01-05","latest":"第20话徒手抓玄雷"},{"name":"白莲妖姬","url":"mh123/comic/25464.html","cover":"https://img.detatu.com/upload/vod/2019-07-03/15621459896.jpg","time":"2020-01-05","latest":"第49话华丽变身开启逆袭~"},{"name":"王爷你好帅","url":"mh123/comic/28578.html","cover":"https://img.detatu.com/upload/pic/2019-11-09/15732750953.jpg","time":"2020-01-05","latest":"第143话你，不脱掉吗？"},{"name":"盛宠阴阳妃","url":"mh123/comic/28720.html","cover":"https://img.detatu.com/upload/pic/2019-11-23/15744807181.jpg","time":"2020-01-05","latest":"第18话宁愿不害怕"},{"name":"校花的贴身特种兵","url":"mh123/comic/25351.html","cover":"https://img.detatu.com/upload/vod/2019-06-21/156110914416.jpg","time":"2020-01-05","latest":"第49话"},{"name":"风水天师在都市","url":"mh123/comic/22607.html","cover":"https://img.detatu.com/upload/vod/2019-03-21/15531431850.jpg","time":"2020-01-05","latest":"第119话小天师"},{"name":"美味佳妻","url":"mh123/comic/25857.html","cover":"https://img.detatu.com/upload/vod/2019-07-26/15641338961.jpg","time":"2020-01-05","latest":"第46话要不要留下习梦梦"},{"name":"仙尊奶爸当赘婿","url":"mh123/comic/28778.html","cover":"https://img.detatu.com/upload/pic/2019-12-01/15751757527.jpg","time":"2020-01-05","latest":"第16话白家震怒，交出沈异"},{"name":"宠你入骨：这豪门，我不嫁了","url":"mh123/comic/28053.html","cover":"https://img.detatu.com/upload/vod/2019-10-11/15707838405.jpg","time":"2020-01-05","latest":"第120话江南大桥出事了"},{"name":"剑玲珑","url":"mh123/comic/25809.html","cover":"https://img.detatu.com/upload/pic/2019-11-01/15725451975.jpg","time":"2020-01-05","latest":"第49话战斗"},{"name":"我要充电","url":"mh123/comic/21947.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/154679437810.jpg","time":"2020-01-05","latest":"第九话"},{"name":"皇帝陛下的天价宝贝","url":"mh123/comic/28777.html","cover":"https://img.detatu.com/upload/pic/2019-12-01/15751757526.jpg","time":"2020-01-05","latest":"第16话王后我们再生个王子吧"},{"name":"嚣张狂妃","url":"mh123/comic/24171.html","cover":"https://img.detatu.com/upload/vod/2019-03-27/15536754731.jpg","time":"2020-01-05","latest":"第115话愿赌服输！"},{"name":"国民校草是女生","url":"mh123/comic/27859.html","cover":"https://img.detatu.com/upload/vod/2019-10-01/15699052140.jpg","time":"2020-01-05","latest":"第二十话 帮秦总解皮带"},{"name":"校园狂师","url":"mh123/comic/26338.html","cover":"https://img.detatu.com/upload/vod/2019-08-09/15653394750.jpg","time":"2020-01-05","latest":"付费通知"},{"name":"我穿越成了恶毒皇后","url":"mh123/comic/19677.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/15467936469.jpg","time":"2020-01-05","latest":"第97话为何帮我"},{"name":"漫画壁纸日签","url":"mh123/comic/29348.html","cover":"https://img.detatu.com/upload/pic/2020-01-01/15778710723.jpg","time":"2020-01-05","latest":"第5话1月5日"},{"name":"兽宠女皇","url":"mh123/comic/24026.html","cover":"https://img.detatu.com/upload/vod/2019-03-22/15532456208.jpg","time":"2020-01-05","latest":"第92话你保护世界，我保护你"},{"name":"步步毒谋:血凰归来","url":"mh123/comic/29275.html","cover":"https://img.detatu.com/upload/pic/2019-12-28/157747444811.jpg","time":"2020-01-05","latest":"第4话入宫"},{"name":"极品败家子","url":"mh123/comic/24844.html","cover":"https://img.detatu.com/upload/vod/2019-05-01/15566873610.jpg","time":"2020-01-05","latest":"第80话洞房花烛夜"},{"name":"怦然心动的秘密","url":"mh123/comic/28667.html","cover":"https://img.detatu.com/upload/pic/2019-11-19/15741380483.jpg","time":"2020-01-05","latest":"第44话一起穿越去修真3"},{"name":"末日刁民","url":"mh123/comic/29407.html","cover":"https://img.detatu.com/upload/pic/2020-01-05/15781618770.jpg","time":"2020-01-05","latest":"第2话全城蔓延"},{"name":"重生巨星：凌少宠上瘾","url":"mh123/comic/26415.html","cover":"https://img.detatu.com/upload/vod/2019-08-13/156563429819.jpg","time":"2020-01-05","latest":"第43话截胡"},{"name":"兄控公爵嫁不得","url":"mh123/comic/25246.html","cover":"https://img.detatu.com/upload/vod/2019-06-10/15601403253.jpg","time":"2020-01-05","latest":"第72话哥哥是人质？"},{"name":"校园护花高手","url":"mh123/comic/26653.html","cover":"https://img.detatu.com/upload/vod/2019-08-21/15663573932.jpg","time":"2020-01-05","latest":"第42话田老师我坦白"},{"name":"邵叔叔家的小野猫","url":"mh123/comic/28650.html","cover":"https://img.detatu.com/upload/pic/2019-11-18/15740666329.jpg","time":"2020-01-05","latest":"第35话太不要脸"},{"name":"恶魔爱上小猫咪","url":"mh123/comic/27932.html","cover":"https://img.detatu.com/upload/vod/2019-10-06/15703439501.jpg","time":"2020-01-05","latest":"第65话校园告白圣地"},{"name":"异世界建国记","url":"mh123/comic/16991.html","cover":"https://oss.mkzcdn.com/comic/cover/20191224/5e01b7967186a-270x360.jpg!cover-400","time":"2020-01-05","latest":"第26-1话"},{"name":"爱神APP","url":"mh123/comic/25355.html","cover":"https://img.detatu.com/upload/vod/2019-06-21/15611091285.jpg","time":"2020-01-05","latest":"第58话载具库"},{"name":"都市之逆天仙尊","url":"mh123/comic/28491.html","cover":"https://img.detatu.com/upload/pic/2019-11-01/15726002262.jpg","time":"2020-01-05","latest":"第21话不用为我担心"},{"name":"无缘佛","url":"mh123/comic/28076.html","cover":"https://img.detatu.com/upload/vod/2019-10-13/15709541221.jpg","time":"2020-01-05","latest":"梦回八百年前，隔世再重逢"},{"name":"天火大道","url":"mh123/comic/18254.html","cover":"https://img.detatu.com/upload/pic/2019-12-12/15761230410.jpg","time":"2020-01-05","latest":"第45话 转机"},{"name":"暗夜无常","url":"mh123/comic/23912.html","cover":"https://img.detatu.com/upload/pic/2019-12-01/15751757511.jpg","time":"2020-01-05","latest":"第八十七话：能不能给单身狗留面子"},{"name":"天价宠妻总裁夫人休想逃","url":"mh123/comic/15445.html","cover":"https://img.detatu.com/upload/vod/2019-03-23/15533312820.jpg","time":"2020-01-05","latest":"第189话敢动我的女人"},{"name":"冷婚狂爱","url":"mh123/comic/21766.html","cover":"https://img.detatu.com/upload/vod/2019-03-22/15532354430.jpg","time":"2020-01-05","latest":"第48话你就是药"},{"name":"Buy Spring","url":"mh123/comic/28394.html","cover":"https://img.detatu.com/upload/pic/2019-10-26/15720733877.jpg","time":"2020-01-05","latest":"第04话"},{"name":"这个兵王很嚣张","url":"mh123/comic/24522.html","cover":"https://img.detatu.com/upload/vod/2019-04-10/15548692540.jpg","time":"2020-01-05","latest":"第82话我就是很嚣张"},{"name":"火星猎人V5","url":"mh123/comic/22235.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/15467944466.jpg","time":"2020-01-05","latest":"第11话"},{"name":"王牌男神有点甜","url":"mh123/comic/29277.html","cover":"https://img.detatu.com/upload/pic/2019-12-28/157747444913.jpg","time":"2020-01-05","latest":"情人？"},{"name":"亿万盛宠只为你","url":"mh123/comic/29303.html","cover":"https://img.detatu.com/upload/pic/2019-12-29/15776045795.jpg","time":"2020-01-05","latest":"007"},{"name":"魔女","url":"mh123/comic/29370.html","cover":"https://img.detatu.com/upload/pic/2020-01-03/157798912112.jpg","time":"2020-01-05","latest":"第25话"},{"name":"蛊仙奶爸","url":"mh123/comic/25775.html","cover":"https://img.detatu.com/upload/pic/2019-11-10/15733580971.jpg","time":"2020-01-05","latest":"第69话 拦路打劫"},{"name":"霸凰传说","url":"mh123/comic/19128.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/154679354713.jpg","time":"2020-01-05","latest":"131 不得了的客人"},{"name":"冰山学长不好惹","url":"mh123/comic/20369.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/15467939804.jpg","time":"2020-01-05","latest":"撇清关系？！"},{"name":"无氧之爱","url":"mh123/comic/26259.html","cover":"https://img.detatu.com/upload/vod/2019-08-07/15651136330.jpg","time":"2020-01-05","latest":"第二十九话 结束和开始③"},{"name":"蝉落千机","url":"mh123/comic/21260.html","cover":"https://img.detatu.com/upload/pic/2019-12-14/15763038641.jpg","time":"2020-01-05","latest":"第一百二十八话"},{"name":"夺命粉丝线","url":"mh123/comic/21828.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/15467943505.jpg","time":"2020-01-05","latest":"第57话"},{"name":"超脑残鉴定","url":"mh123/comic/21640.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/154679430218.jpg","time":"2020-01-05","latest":"169 小朋友3"},{"name":"联合特工队","url":"mh123/comic/21250.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/15467942136.jpg","time":"2020-01-05","latest":"13"},{"name":"冥界你不知道的事","url":"mh123/comic/20696.html","cover":"https://img.detatu.com/upload/vod/2019-03-25/15534506207.jpg","time":"2020-01-05","latest":"me too"},{"name":"西缘记","url":"mh123/comic/19968.html","cover":"https://img.detatu.com/upload/vod/2019-01-07/154679374618.jpg","time":"2020-01-05","latest":"第十八章：因缘线03"},{"name":"氪金封神","url":"mh123/comic/27462.html","cover":"https://img.detatu.com/upload/vod/2019-09-18/15687978752.jpg","time":"2020-01-05","latest":"第四十九话 冥灵神庙"},{"name":"乱世迟爱:我的男爵大人","url":"mh123/comic/27756.html","cover":"https://img.detatu.com/upload/vod/2019-09-29/15697439470.jpg","time":"2020-01-05","latest":"第57话又如妖妇人，绸缪蛊其夫"}]
     */

    @Id
    private Long id;

    @Index(unique = true)
    private String mhlb;

    @ToMany(referencedJoinProperty = "mhlbId")
    private List<TestListBean> list;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1254109139)
    private transient TestBeanDao myDao;

    @Generated(hash = 536440338)
    public TestBean(Long id, String mhlb) {
        this.id = id;
        this.mhlb = mhlb;
    }

    @Generated(hash = 2087637710)
    public TestBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMhlb() {
        return this.mhlb;
    }

    public void setMhlb(String mhlb) {
        this.mhlb = mhlb;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1832635647)
    public List<TestListBean> getList() {
        if (list == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TestListBeanDao targetDao = daoSession.getTestListBeanDao();
            List<TestListBean> listNew = targetDao._queryTestBean_List(id);
            synchronized (this) {
                if (list == null) {
                    list = listNew;
                }
            }
        }
        return list;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 589833612)
    public synchronized void resetList() {
        list = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1819053713)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTestBeanDao() : null;
    }


}
