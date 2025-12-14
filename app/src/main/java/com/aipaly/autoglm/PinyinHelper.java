package com.aipaly.autoglm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinyinHelper {
    // 从提供的pinyin_dict_notone.js转换而来的拼音字典
    private static final Map<String, String> PINYIN_DICT = new HashMap<>();
    
    static {
        // 单字拼音词库
        PINYIN_DICT.put("a", "阿啊呵腌嗄吖锕");
        PINYIN_DICT.put("e", "额阿俄恶鹅遏鄂厄饿峨扼娥鳄哦蛾噩愕讹锷垩婀鹗萼谔莪腭锇颚呃阏屙苊轭");
        PINYIN_DICT.put("ai", "爱埃艾碍癌哀挨矮隘蔼唉皑哎霭捱暧嫒嗳瑷嗌锿砹");
        PINYIN_DICT.put("ei", "诶");
        PINYIN_DICT.put("xi", "系西席息希习吸喜细析戏洗悉锡溪惜稀袭夕洒晰昔牺腊烯熙媳栖膝隙犀蹊硒兮熄曦禧嬉玺奚汐徙羲铣淅嘻歙熹矽蟋郗唏皙隰樨浠忾蜥檄郄翕阋鳃舾屣葸螅咭粞觋欷僖醯鼷裼穸饩舄禊诶菥蓰");
        PINYIN_DICT.put("yi", "一以已意议义益亿易医艺食依移衣异伊仪宜射遗疑毅谊亦疫役忆抑尾乙译翼蛇溢椅沂泄逸蚁夷邑怡绎彝裔姨熠贻矣屹颐倚诣胰奕翌疙弈轶蛾驿壹猗臆弋铱旖漪迤佚翊诒怿痍懿饴峄揖眙镒仡黟肄咿翳挹缢呓刈咦嶷羿钇殪荑薏蜴镱噫癔苡悒嗌瘗衤佾埸圯舣酏劓");
        PINYIN_DICT.put("an", "安案按岸暗鞍氨俺胺铵谙庵黯鹌桉埯犴揞厂广");
        PINYIN_DICT.put("han", "厂汉韩含旱寒汗涵函喊憾罕焊翰邯撼瀚憨捍酣悍鼾邗颔蚶晗菡旰顸犴焓撖");
        PINYIN_DICT.put("ang", "昂仰盎肮");
        PINYIN_DICT.put("ao", "奥澳傲熬凹鳌敖遨鏖袄坳翱嗷拗懊岙螯骜獒鏊艹媪廒聱");
        PINYIN_DICT.put("wa", "瓦挖娃洼袜蛙凹哇佤娲呙腽");
        PINYIN_DICT.put("yu", "于与育余预域予遇奥语誉玉鱼雨渔裕愈娱欲吁舆宇羽逾豫郁寓吾狱喻御浴愉禹俞邪榆愚渝尉淤虞屿峪粥驭瑜禺毓钰隅芋熨瘀迂煜昱汩於臾盂聿竽萸妪腴圄谕觎揄龉谀俣馀庾妤瘐鬻欤鹬阈嵛雩鹆圉蜮伛纡窬窳饫蓣狳肀舁蝓燠");
        PINYIN_DICT.put("niu", "牛纽扭钮拗妞忸狃");
        PINYIN_DICT.put("o", "哦噢喔");
        PINYIN_DICT.put("ba", "把八巴拔伯吧坝爸霸罢芭跋扒叭靶疤笆耙鲅粑岜灞钯捌菝魃茇");
        PINYIN_DICT.put("pa", "怕帕爬扒趴琶啪葩耙杷钯筢");
        PINYIN_DICT.put("pi", "被批副否皮坏辟啤匹披疲罢僻毗坯脾譬劈媲屁琵邳裨痞癖陂丕枇噼霹吡纰砒铍淠郫埤濞睥芘蚍圮鼙罴蜱疋貔仳庀擗甓陴");
        PINYIN_DICT.put("bi", "比必币笔毕秘避闭佛辟壁弊彼逼碧鼻臂蔽拂泌璧庇痹毙弼匕鄙陛裨贲敝蓖吡篦纰俾铋毖筚荸薜婢哔跸濞秕荜愎睥妣芘箅髀畀滗狴萆嬖襞舭");
        PINYIN_DICT.put("bai", "百白败摆伯拜柏佰掰呗擘捭稗");
        PINYIN_DICT.put("bo", "波博播勃拨薄佛伯玻搏柏泊舶剥渤卜驳簿脖膊簸菠礴箔铂亳钵帛擘饽跛钹趵檗啵鹁擗踣");
        PINYIN_DICT.put("bei", "北被备倍背杯勃贝辈悲碑臂卑悖惫蓓陂钡狈呗焙碚褙庳鞴孛鹎邶鐾");
        PINYIN_DICT.put("ban", "办版半班般板颁伴搬斑扮拌扳瓣坂阪绊钣瘢舨癍");
        PINYIN_DICT.put("pan", "判盘番潘攀盼拚畔胖叛拌蹒磐爿蟠泮袢襻丬");
        PINYIN_DICT.put("bin", "份宾频滨斌彬濒殡缤鬓槟摈膑玢镔豳髌傧");
        PINYIN_DICT.put("bang", "帮邦彭旁榜棒膀镑绑傍磅蚌谤梆浜蒡");
        PINYIN_DICT.put("pang", "旁庞乓磅螃彷滂逄耪");
        PINYIN_DICT.put("beng", "泵崩蚌蹦迸绷甭嘣甏堋");
        PINYIN_DICT.put("bao", "报保包宝暴胞薄爆炮饱抱堡剥鲍曝葆瀑豹刨褒雹孢苞煲褓趵鸨龅勹");
        PINYIN_DICT.put("bu", "不部步布补捕堡埔卜埠簿哺怖钚卟瓿逋晡醭钸");
        PINYIN_DICT.put("pu", "普暴铺浦朴堡葡谱埔扑仆蒲曝瀑溥莆圃璞濮菩蹼匍噗氆攵镨攴镤");
        PINYIN_DICT.put("mian", "面棉免绵缅勉眠冕娩腼渑湎沔黾宀眄");
        PINYIN_DICT.put("po", "破繁坡迫颇朴泊婆泼魄粕鄱珀陂叵笸泺皤钋钷");
        PINYIN_DICT.put("fan", "反范犯繁饭泛翻凡返番贩烦拚帆樊藩矾梵蕃钒幡畈蘩蹯燔");
        PINYIN_DICT.put("fu", "府服副负富复福夫妇幅付扶父符附腐赴佛浮覆辅傅伏抚赋辐腹弗肤阜袱缚甫氟斧孚敷俯拂俘咐腑孵芙涪釜脯茯馥宓绂讣呋罘麸蝠匐芾蜉跗凫滏蝮驸绋蚨砩桴赙菔呒趺苻拊阝鲋怫稃郛莩幞祓艴黻黼鳆");
        PINYIN_DICT.put("ben", "本体奔苯笨夯贲锛畚坌");
        PINYIN_DICT.put("feng", "风丰封峰奉凤锋冯逢缝蜂枫疯讽烽俸沣酆砜葑唪");
        PINYIN_DICT.put("bian", "变便边编遍辩鞭辨贬匾扁卞汴辫砭苄蝙鳊弁窆笾煸褊碥忭缏");
        PINYIN_DICT.put("pian", "便片篇偏骗翩扁骈胼蹁谝犏缏");
        PINYIN_DICT.put("zhen", "镇真针圳振震珍阵诊填侦臻贞枕桢赈祯帧甄斟缜箴疹砧榛鸩轸稹溱蓁胗椹朕畛浈");
        PINYIN_DICT.put("biao", "表标彪镖裱飚膘飙镳婊骠飑杓髟鳔灬瘭");
        PINYIN_DICT.put("piao", "票朴漂飘嫖瓢剽缥殍瞟骠嘌莩螵");
        PINYIN_DICT.put("huo", "和活或货获火伙惑霍祸豁嚯藿锪蠖钬耠镬夥灬劐攉");
        PINYIN_DICT.put("bie", "别鳖憋瘪蹩");
        PINYIN_DICT.put("min", "民敏闽闵皿泯岷悯珉抿黾缗玟愍苠鳘");
        PINYIN_DICT.put("fen", "分份纷奋粉氛芬愤粪坟汾焚酚吩忿棼玢鼢瀵偾鲼");
        PINYIN_DICT.put("bing", "并病兵冰屏饼炳秉丙摒柄槟禀枋邴冫");
        PINYIN_DICT.put("geng", "更耕颈庚耿梗埂羹哽赓绠鲠");
        PINYIN_DICT.put("fang", "方放房防访纺芳仿坊妨肪邡舫彷枋鲂匚钫");
        PINYIN_DICT.put("xian", "现先县见线限显险献鲜洗宪纤陷闲贤仙衔掀咸嫌掺羡弦腺痫娴舷馅酰铣冼涎暹籼锨苋蚬跹岘藓燹鹇氙莶霰跣猃彡祆筅");
        PINYIN_DICT.put("fou", "不否缶");
        PINYIN_DICT.put("ca", "拆擦嚓礤");
        PINYIN_DICT.put("cha", "查察差茶插叉刹茬楂岔诧碴嚓喳姹杈汊衩搽槎镲苴檫馇锸猹");
        PINYIN_DICT.put("cai", "才采财材菜彩裁蔡猜踩睬");
        PINYIN_DICT.put("can", "参残餐灿惨蚕掺璨惭粲孱骖黪");
        PINYIN_DICT.put("shen", "信深参身神什审申甚沈伸慎渗肾绅莘呻婶娠砷蜃哂椹葚吲糁渖诜谂矧胂");
        PINYIN_DICT.put("cen", "参岑涔");
        PINYIN_DICT.put("san", "三参散伞叁糁馓毵");
        PINYIN_DICT.put("cang", "藏仓苍沧舱臧伧");
        PINYIN_DICT.put("zang", "藏脏葬赃臧奘驵");
        PINYIN_DICT.put("chen", "称陈沈沉晨琛臣尘辰衬趁忱郴宸谌碜嗔抻榇伧谶龀肜");
        PINYIN_DICT.put("cao", "草操曹槽糙嘈漕螬艚屮");
        PINYIN_DICT.put("ce", "策测册侧厕栅恻");
        PINYIN_DICT.put("ze", "责则泽择侧咋啧仄箦赜笮舴昃迮帻");
        PINYIN_DICT.put("zhai", "债择齐宅寨侧摘窄斋祭翟砦瘵哜");
        PINYIN_DICT.put("dao", "到道导岛倒刀盗稻蹈悼捣叨祷焘氘纛刂帱忉");
        PINYIN_DICT.put("ceng", "层曾蹭噌");
        PINYIN_DICT.put("zha", "查扎炸诈闸渣咋乍榨楂札栅眨咤柞喳喋铡蚱吒怍砟揸痄哳齄");
        PINYIN_DICT.put("chai", "差拆柴钗豺侪虿瘥");
        PINYIN_DICT.put("ci", "次此差词辞刺瓷磁兹慈茨赐祠伺雌疵鹚糍吡粢");
        PINYIN_DICT.put("zi", "资自子字齐咨滋仔姿紫兹孜淄籽梓鲻渍姊吱秭恣甾孳訾滓锱辎趑龇赀眦缁吡笫谘嵫髭茈粢觜耔");
        PINYIN_DICT.put("cuo", "措错磋挫搓撮蹉锉厝嵯痤矬瘥脞鹾");
        PINYIN_DICT.put("chan", "产单阐崭缠掺禅颤铲蝉搀潺蟾馋忏婵孱觇廛谄谗澶骣羼躔蒇冁");
        PINYIN_DICT.put("shan", "山单善陕闪衫擅汕扇掺珊禅删膳缮赡鄯栅煽姗跚鳝嬗潸讪舢苫疝掸膻钐剡蟮芟埏彡骟");
        PINYIN_DICT.put("zhan", "展战占站崭粘湛沾瞻颤詹斩盏辗绽毡栈蘸旃谵搌");
        PINYIN_DICT.put("xin", "新心信辛欣薪馨鑫芯锌忻莘昕衅歆囟忄镡");
        PINYIN_DICT.put("lian", "联连练廉炼脸莲恋链帘怜涟敛琏镰濂楝鲢殓潋裢裣臁奁莶蠊蔹");
        PINYIN_DICT.put("chang", "场长厂常偿昌唱畅倡尝肠敞倘猖娼淌裳徜昶怅嫦菖鲳阊伥苌氅惝鬯");
        PINYIN_DICT.put("zhang", "长张章障涨掌帐胀彰丈仗漳樟账杖璋嶂仉瘴蟑獐幛鄣嫜");
        PINYIN_DICT.put("chao", "超朝潮炒钞抄巢吵剿绰嘲晁焯耖怊");
        PINYIN_DICT.put("zhao", "着照招找召朝赵兆昭肇罩钊沼嘲爪诏濯啁棹笊");
        PINYIN_DICT.put("zhou", "调州周洲舟骤轴昼宙粥皱肘咒帚胄绉纣妯啁诌繇碡籀酎荮");
        PINYIN_DICT.put("che", "车彻撤尺扯澈掣坼砗屮");
        PINYIN_DICT.put("ju", "车局据具举且居剧巨聚渠距句拒俱柜菊拘炬桔惧矩鞠驹锯踞咀瞿枸掬沮莒橘飓疽钜趄踽遽琚龃椐苣裾榘狙倨榉苴讵雎锔窭鞫犋屦醵");
        PINYIN_DICT.put("cheng", "成程城承称盛抢乘诚呈净惩撑澄秤橙骋逞瞠丞晟铛埕塍蛏柽铖酲裎枨");
        PINYIN_DICT.put("rong", "容荣融绒溶蓉熔戎榕茸冗嵘肜狨蝾");
        PINYIN_DICT.put("sheng", "生声升胜盛乘圣剩牲甸省绳笙甥嵊晟渑眚");
        PINYIN_DICT.put("deng", "等登邓灯澄凳瞪蹬噔磴嶝镫簦戥");
        PINYIN_DICT.put("zhi", "制之治质职只志至指织支值知识直致执置止植纸拓智殖秩旨址滞氏枝芝脂帜汁肢挚稚酯掷峙炙栉侄芷窒咫吱趾痔蜘郅桎雉祉郦陟痣蛭帙枳踯徵胝栀贽祗豸鸷摭轵卮轾彘觯絷跖埴夂黹忮骘膣踬");
        PINYIN_DICT.put("zheng", "政正证争整征郑丁症挣蒸睁铮筝拯峥怔诤狰徵钲");
        PINYIN_DICT.put("tang", "堂唐糖汤塘躺趟倘棠烫淌膛搪镗傥螳溏帑羰樘醣螗耥铴瑭");
        PINYIN_DICT.put("chi", "持吃池迟赤驰尺斥齿翅匙痴耻炽侈弛叱啻坻眙嗤墀哧茌豉敕笞饬踟蚩柢媸魑篪褫彳鸱螭瘛眵傺");
        PINYIN_DICT.put("shi", "是时实事市十使世施式势视识师史示石食始士失适试什泽室似诗饰殖释驶氏硕逝湿蚀狮誓拾尸匙仕柿矢峙侍噬嗜栅拭嘘屎恃轼虱耆舐莳铈谥炻豕鲥饣螫酾筮埘弑礻蓍鲺贳");
        PINYIN_DICT.put("qi", "企其起期气七器汽奇齐启旗棋妻弃揭枝歧欺骑契迄亟漆戚岂稽岐琦栖缉琪泣乞砌祁崎绮祺祈凄淇杞脐麒圻憩芪伎俟畦耆葺沏萋骐鳍綦讫蕲屺颀亓碛柒啐汔綮萁嘁蛴槭欹芑桤丌蜞");
        PINYIN_DICT.put("chuai", "揣踹啜搋膪");
        PINYIN_DICT.put("tuo", "托脱拓拖妥驼陀沱鸵驮唾椭坨佗砣跎庹柁橐乇铊沲酡鼍箨柝");
        PINYIN_DICT.put("duo", "多度夺朵躲铎隋咄堕舵垛惰哆踱跺掇剁柁缍沲裰哚隳");
        PINYIN_DICT.put("xue", "学血雪削薛穴靴谑噱鳕踅泶彐");
        PINYIN_DICT.put("chong", "重种充冲涌崇虫宠忡憧舂茺铳艟");
        PINYIN_DICT.put("chou", "筹抽绸酬愁丑臭仇畴稠瞅踌惆俦瘳雠帱");
        PINYIN_DICT.put("qiu", "求球秋丘邱仇酋裘龟囚遒鳅虬蚯泅楸湫犰逑巯艽俅蝤赇鼽糗");
        PINYIN_DICT.put("xiu", "修秀休宿袖绣臭朽锈羞嗅岫溴庥馐咻髹鸺貅");
        PINYIN_DICT.put("chu", "出处础初助除储畜触楚厨雏矗橱锄滁躇怵绌搐刍蜍黜杵蹰亍樗憷楮");
        PINYIN_DICT.put("tuan", "团揣湍疃抟彖");
        PINYIN_DICT.put("zhui", "追坠缀揣椎锥赘惴隹骓缒");
        PINYIN_DICT.put("chuan", "传川船穿串喘椽舛钏遄氚巛舡");
        PINYIN_DICT.put("zhuan", "专转传赚砖撰篆馔啭颛");
        PINYIN_DICT.put("yuan", "元员院原源远愿园援圆缘袁怨渊苑宛冤媛猿垣沅塬垸鸳辕鸢瑗圜爰芫鼋橼螈眢箢掾");
        PINYIN_DICT.put("cuan", "窜攒篡蹿撺爨汆镩");
        PINYIN_DICT.put("chuang", "创床窗闯幢疮怆");
        PINYIN_DICT.put("zhuang", "装状庄壮撞妆幢桩奘僮戆");
        PINYIN_DICT.put("chui", "吹垂锤炊椎陲槌捶棰");
        PINYIN_DICT.put("chun", "春纯醇淳唇椿蠢鹑朐莼肫蝽");
        PINYIN_DICT.put("zhun", "准屯淳谆肫窀");
        PINYIN_DICT.put("cu", "促趋趣粗簇醋卒蹴猝蹙蔟殂徂");
        PINYIN_DICT.put("dun", "吨顿盾敦蹲墩囤沌钝炖盹遁趸砘礅");
        PINYIN_DICT.put("qu", "区去取曲趋渠趣驱屈躯衢娶祛瞿岖龋觑朐蛐癯蛆苣阒诎劬蕖蘧氍黢蠼璩麴鸲磲");
        PINYIN_DICT.put("xu", "需许续须序徐休蓄畜虚吁绪叙旭邪恤墟栩絮圩婿戌胥嘘浒煦酗诩朐盱蓿溆洫顼勖糈砉醑");
        PINYIN_DICT.put("chuo", "辍绰戳淖啜龊踔辶");
        PINYIN_DICT.put("zu", "组族足祖租阻卒俎诅镞菹");
        PINYIN_DICT.put("ji", "济机其技基记计系期际及集级几给积极己纪即继击既激绩急奇吉季齐疾迹鸡剂辑籍寄挤圾冀亟寂暨脊跻肌稽忌饥祭缉棘矶汲畸姬藉瘠骥羁妓讥稷蓟悸嫉岌叽伎鲫诘楫荠戟箕霁嵇觊麂畿玑笈犄芨唧屐髻戢佶偈笄跽蒺乩咭赍嵴虮掎齑殛鲚剞洎丌墼蕺彐芰哜");
        PINYIN_DICT.put("cong", "从丛匆聪葱囱琮淙枞骢苁璁");
        PINYIN_DICT.put("zong", "总从综宗纵踪棕粽鬃偬枞腙");
        PINYIN_DICT.put("cou", "凑辏腠楱");
        PINYIN_DICT.put("cui", "衰催崔脆翠萃粹摧璀瘁悴淬啐隹毳榱");
        PINYIN_DICT.put("wei", "为位委未维卫围违威伟危味微唯谓伪慰尾魏韦胃畏帷喂巍萎蔚纬潍尉渭惟薇苇炜圩娓诿玮崴桅偎逶倭猥囗葳隗痿猬涠嵬韪煨艉隹帏闱洧沩隈鲔軎");
        PINYIN_DICT.put("cun", "村存寸忖皴");
        PINYIN_DICT.put("zuo", "作做座左坐昨佐琢撮祚柞唑嘬酢怍笮阼胙");
        PINYIN_DICT.put("zuan", "钻纂攥缵躜");
        PINYIN_DICT.put("da", "大达打答搭沓瘩惮嗒哒耷鞑靼褡笪怛妲");
        PINYIN_DICT.put("dai", "大代带待贷毒戴袋歹呆隶逮岱傣棣怠殆黛甙埭诒绐玳呔迨");
        PINYIN_DICT.put("tai", "大台太态泰抬胎汰钛苔薹肽跆邰鲐酞骀炱");
        PINYIN_DICT.put("ta", "他它她拓塔踏塌榻沓漯獭嗒挞蹋趿遢铊鳎溻闼");
        PINYIN_DICT.put("dan", "但单石担丹胆旦弹蛋淡诞氮郸耽殚惮儋眈疸澹掸膻啖箪聃萏瘅赕");
        PINYIN_DICT.put("lu", "路六陆录绿露鲁卢炉鹿禄赂芦庐碌麓颅泸卤潞鹭辘虏璐漉噜戮鲈掳橹轳逯渌蓼撸鸬栌氇胪镥簏舻辂垆");
        PINYIN_DICT.put("tan", "谈探坦摊弹炭坛滩贪叹谭潭碳毯瘫檀痰袒坍覃忐昙郯澹钽锬");
        PINYIN_DICT.put("ren", "人任认仁忍韧刃纫饪妊荏稔壬仞轫亻衽");
        PINYIN_DICT.put("jie", "家结解价界接节她届介阶街借杰洁截姐揭捷劫戒皆竭桔诫楷秸睫藉拮芥诘碣嗟颉蚧孑婕疖桀讦疥偈羯袷哜喈卩鲒骱");
        PINYIN_DICT.put("yan", "研严验演言眼烟沿延盐炎燕岩宴艳颜殷彦掩淹阎衍铅雁咽厌焰堰砚唁焉晏檐蜒奄俨腌妍谚兖筵焱偃闫嫣鄢湮赝胭琰滟阉魇酽郾恹崦芫剡鼹菸餍埏谳讠厣罨");
        PINYIN_DICT.put("dang", "当党档荡挡宕砀铛裆凼菪谠");
        PINYIN_DICT.put("tao", "套讨跳陶涛逃桃萄淘掏滔韬叨洮啕绦饕鼗");
        PINYIN_DICT.put("tiao", "条调挑跳迢眺苕窕笤佻啁粜髫铫祧龆蜩鲦");
        PINYIN_DICT.put("te", "特忑忒铽慝");
        PINYIN_DICT.put("de", "的地得德底锝");
        PINYIN_DICT.put("dei", "得");
        PINYIN_DICT.put("di", "的地第提低底抵弟迪递帝敌堤蒂缔滴涤翟娣笛棣荻谛狄邸嘀砥坻诋嫡镝碲骶氐柢籴羝睇觌");
        PINYIN_DICT.put("ti", "体提题弟替梯踢惕剔蹄棣啼屉剃涕锑倜悌逖嚏荑醍绨鹈缇裼");
        PINYIN_DICT.put("tui", "推退弟腿褪颓蜕忒煺");
        PINYIN_DICT.put("you", "有由又优游油友右邮尤忧幼犹诱悠幽佑釉柚铀鱿囿酉攸黝莠猷蝣疣呦蚴莸莜铕宥繇卣牖鼬尢蚰侑");
        PINYIN_DICT.put("dian", "电点店典奠甸碘淀殿垫颠滇癫巅惦掂癜玷佃踮靛钿簟坫阽");
        PINYIN_DICT.put("tian", "天田添填甜甸恬腆佃舔钿阗忝殄畋栝掭");
        PINYIN_DICT.put("zhu", "主术住注助属逐宁著筑驻朱珠祝猪诸柱竹铸株瞩嘱贮煮烛苎褚蛛拄铢洙竺蛀渚伫杼侏澍诛茱箸炷躅翥潴邾槠舳橥丶瘃麈疰");
        PINYIN_DICT.put("nian", "年念酿辗碾廿捻撵拈蔫鲶埝鲇辇黏");
        PINYIN_DICT.put("diao", "调掉雕吊钓刁貂凋碉鲷叼铫铞");
        PINYIN_DICT.put("yao", "要么约药邀摇耀腰遥姚窑瑶咬尧钥谣肴夭侥吆疟妖幺杳舀窕窈曜鹞爻繇徭轺铫鳐崾珧");
        PINYIN_DICT.put("die", "跌叠蝶迭碟爹谍牒耋佚喋堞瓞鲽垤揲蹀");
        PINYIN_DICT.put("she", "设社摄涉射折舍蛇拾舌奢慑赦赊佘麝歙畲厍猞揲滠");
        PINYIN_DICT.put("ye", "业也夜叶射野液冶喝页爷耶邪咽椰烨掖拽曳晔谒腋噎揶靥邺铘揲");
        PINYIN_DICT.put("xie", "些解协写血叶谢械鞋胁斜携懈契卸谐泄蟹邪歇泻屑挟燮榭蝎撷偕亵楔颉缬邂鲑瀣勰榍薤绁渫廨獬躞");
        PINYIN_DICT.put("zhe", "这者着著浙折哲蔗遮辙辄柘锗褶蜇蛰鹧谪赭摺乇磔螫");
        PINYIN_DICT.put("ding", "定订顶丁鼎盯钉锭叮仃铤町酊啶碇腚疔玎耵");
        PINYIN_DICT.put("diu", "丢铥");
        PINYIN_DICT.put("ting", "听庭停厅廷挺亭艇婷汀铤烃霆町蜓葶梃莛");
        PINYIN_DICT.put("dong", "动东董冬洞懂冻栋侗咚峒氡恫胴硐垌鸫岽胨");
        PINYIN_DICT.put("tong", "同通统童痛铜桶桐筒彤侗佟潼捅酮砼瞳恸峒仝嗵僮垌茼");
        PINYIN_DICT.put("zhong", "中重种众终钟忠仲衷肿踵冢盅蚣忪锺舯螽夂");
        PINYIN_DICT.put("dou", "都斗读豆抖兜陡逗窦渎蚪痘蔸钭篼");
        PINYIN_DICT.put("du", "度都独督读毒渡杜堵赌睹肚镀渎笃竺嘟犊妒牍蠹椟黩芏髑");
        PINYIN_DICT.put("duan", "断段短端锻缎煅椴簖");
        PINYIN_DICT.put("dui", "对队追敦兑堆碓镦怼憝");
        PINYIN_DICT.put("rui", "瑞兑锐睿芮蕊蕤蚋枘");
        PINYIN_DICT.put("yue", "月说约越乐跃兑阅岳粤悦曰钥栎钺樾瀹龠哕刖");
        PINYIN_DICT.put("tun", "吞屯囤褪豚臀饨暾氽");
        PINYIN_DICT.put("hui", "会回挥汇惠辉恢徽绘毁慧灰贿卉悔秽溃荟晖彗讳诲珲堕诙蕙晦睢麾烩茴喙桧蛔洄浍虺恚蟪咴隳缋哕");
        PINYIN_DICT.put("wu", "务物无五武午吴舞伍污乌误亡恶屋晤悟吾雾芜梧勿巫侮坞毋诬呜钨邬捂鹜兀婺妩於戊鹉浯蜈唔骛仵焐芴鋈庑鼯牾怃圬忤痦迕杌寤阢");
        PINYIN_DICT.put("ya", "亚压雅牙押鸭呀轧涯崖邪芽哑讶鸦娅衙丫蚜碣垭伢氩桠琊揠吖睚痖疋迓岈砑");
        PINYIN_DICT.put("he", "和合河何核盖贺喝赫荷盒鹤吓呵苛禾菏壑褐涸阂阖劾诃颌嗬貉曷翮纥盍");
        PINYIN_DICT.put("wo", "我握窝沃卧挝涡斡渥幄蜗喔倭莴龌肟硪");
        PINYIN_DICT.put("en", "恩摁蒽");
        PINYIN_DICT.put("n", "嗯唔");
        PINYIN_DICT.put("er", "而二尔儿耳迩饵洱贰铒珥佴鸸鲕");
        PINYIN_DICT.put("fa", "发法罚乏伐阀筏砝垡珐");
        PINYIN_DICT.put("quan", "全权券泉圈拳劝犬铨痊诠荃醛蜷颧绻犭筌鬈悛辁畎");
        PINYIN_DICT.put("fei", "费非飞肥废菲肺啡沸匪斐蜚妃诽扉翡霏吠绯腓痱芾淝悱狒榧砩鲱篚镄");
        PINYIN_DICT.put("pei", "配培坏赔佩陪沛裴胚妃霈淠旆帔呸醅辔锫");
        PINYIN_DICT.put("ping", "平评凭瓶冯屏萍苹乒坪枰娉俜鲆");
        PINYIN_DICT.put("fo", "佛");
        PINYIN_DICT.put("hu", "和护许户核湖互乎呼胡戏忽虎沪糊壶葫狐蝴弧瑚浒鹄琥扈唬滹惚祜囫斛笏芴醐猢怙唿戽槲觳煳鹕冱瓠虍岵鹱烀轷");
        PINYIN_DICT.put("ga", "夹咖嘎尬噶旮伽尕钆尜");
        PINYIN_DICT.put("ge", "个合各革格歌哥盖隔割阁戈葛鸽搁胳舸疙铬骼蛤咯圪镉颌仡硌嗝鬲膈纥袼搿塥哿虼");
        PINYIN_DICT.put("ha", "哈蛤铪");
        PINYIN_DICT.put("xia", "下夏峡厦辖霞夹虾狭吓侠暇遐瞎匣瑕唬呷黠硖罅狎瘕柙");
        PINYIN_DICT.put("gai", "改该盖概溉钙丐芥赅垓陔戤");
        PINYIN_DICT.put("hai", "海还害孩亥咳骸骇氦嗨胲醢");
        PINYIN_DICT.put("gan", "干感赶敢甘肝杆赣乾柑尴竿秆橄矸淦苷擀酐绀泔坩旰疳澉");
        PINYIN_DICT.put("gang", "港钢刚岗纲冈杠缸扛肛罡戆筻");
        PINYIN_DICT.put("jiang", "将强江港奖讲降疆蒋姜浆匠酱僵桨绛缰犟豇礓洚茳糨耩");
        PINYIN_DICT.put("hang", "行航杭巷夯吭桁沆绗颃");
        PINYIN_DICT.put("gong", "工公共供功红贡攻宫巩龚恭拱躬弓汞蚣珙觥肱廾");
        PINYIN_DICT.put("hong", "红宏洪轰虹鸿弘哄烘泓訇蕻闳讧荭黉薨");
        PINYIN_DICT.put("guang", "广光逛潢犷胱咣桄");
        PINYIN_DICT.put("qiong", "穷琼穹邛茕筇跫蛩銎");
        PINYIN_DICT.put("gao", "高告搞稿膏糕镐皋羔锆杲郜睾诰藁篙缟槁槔");
        PINYIN_DICT.put("hao", "好号毫豪耗浩郝皓昊皋蒿壕灏嚎濠蚝貉颢嗥薅嚆");
        PINYIN_DICT.put("li", "理力利立里李历例离励礼丽黎璃厉厘粒莉梨隶栗荔沥犁漓哩狸藜罹篱鲤砺吏澧俐骊溧砾莅锂笠蠡蛎痢雳俪傈醴栎郦俚枥喱逦娌鹂戾砬唳坜疠蜊黧猁鬲粝蓠呖跞疬缡鲡鳢嫠詈悝苈篥轹");
        PINYIN_DICT.put("jia", "家加价假佳架甲嘉贾驾嫁夹稼钾挟拮迦伽颊浃枷戛荚痂颉镓笳珈岬胛袈郏葭袷瘕铗跏蛱恝哿");
        PINYIN_DICT.put("luo", "落罗络洛逻螺锣骆萝裸漯烙摞骡咯箩珞捋荦硌雒椤镙跞瘰泺脶猡倮蠃");
        PINYIN_DICT.put("ke", "可科克客刻课颗渴壳柯棵呵坷恪苛咳磕珂稞瞌溘轲窠嗑疴蝌岢铪颏髁蚵缂氪骒钶锞");
        PINYIN_DICT.put("qia", "卡恰洽掐髂袷咭葜");
        PINYIN_DICT.put("gei", "给");
        PINYIN_DICT.put("gen", "根跟亘艮哏茛");
        PINYIN_DICT.put("hen", "很狠恨痕哏");
        PINYIN_DICT.put("gou", "构购够句沟狗钩拘勾苟垢枸篝佝媾诟岣彀缑笱鞲觏遘");
        PINYIN_DICT.put("kou", "口扣寇叩抠佝蔻芤眍筘");
        PINYIN_DICT.put("gu", "股古顾故固鼓骨估谷贾姑孤雇辜菇沽咕呱锢钴箍汩梏痼崮轱鸪牯蛊诂毂鹘菰罟嘏臌觚瞽蛄酤牿鲴");
        PINYIN_DICT.put("pai", "牌排派拍迫徘湃俳哌蒎");
        PINYIN_DICT.put("gua", "括挂瓜刮寡卦呱褂剐胍诖鸹栝呙");
        PINYIN_DICT.put("tou", "投头透偷愉骰亠");
        PINYIN_DICT.put("guai", "怪拐乖");
        PINYIN_DICT.put("kuai", "会快块筷脍蒯侩浍郐蒉狯哙");
        PINYIN_DICT.put("guan", "关管观馆官贯冠惯灌罐莞纶棺斡矜倌鹳鳏盥掼涫");
        PINYIN_DICT.put("wan", "万完晚湾玩碗顽挽弯蔓丸莞皖宛婉腕蜿惋烷琬畹豌剜纨绾脘菀芄箢");
        PINYIN_DICT.put("ne", "呢哪呐讷疒");
        PINYIN_DICT.put("gui", "规贵归轨桂柜圭鬼硅瑰跪龟匮闺诡癸鳜桧皈鲑刽晷傀眭妫炅庋簋刿宄匦");
        PINYIN_DICT.put("jun", "军均俊君峻菌竣钧骏龟浚隽郡筠皲麇捃");
        PINYIN_DICT.put("jiong", "窘炯迥炅冂扃");
        PINYIN_DICT.put("jue", "决绝角觉掘崛诀獗抉爵嚼倔厥蕨攫珏矍蹶谲镢鳜噱桷噘撅橛孓觖劂爝");
        PINYIN_DICT.put("gun", "滚棍辊衮磙鲧绲丨");
        PINYIN_DICT.put("hun", "婚混魂浑昏棍珲荤馄诨溷阍");
        PINYIN_DICT.put("guo", "国过果郭锅裹帼涡椁囗蝈虢聒埚掴猓崞蜾呙馘");
        PINYIN_DICT.put("hei", "黑嘿嗨");
        PINYIN_DICT.put("kan", "看刊勘堪坎砍侃嵌槛瞰阚龛戡凵莰");
        PINYIN_DICT.put("heng", "衡横恒亨哼珩桁蘅");
        PINYIN_DICT.put("mo", "万没么模末冒莫摩墨默磨摸漠脉膜魔沫陌抹寞蘑摹蓦馍茉嘿谟秣蟆貉嫫镆殁耱嬷麽瘼貊貘");
        PINYIN_DICT.put("peng", "鹏朋彭膨蓬碰苹棚捧亨烹篷澎抨硼怦砰嘭蟛堋");
        PINYIN_DICT.put("hou", "后候厚侯猴喉吼逅篌糇骺後鲎瘊堠");
        PINYIN_DICT.put("hua", "化华划话花画滑哗豁骅桦猾铧砉");
        PINYIN_DICT.put("huai", "怀坏淮徊槐踝");
        PINYIN_DICT.put("huan", "还环换欢患缓唤焕幻痪桓寰涣宦垸洹浣豢奂郇圜獾鲩鬟萑逭漶锾缳擐");
        PINYIN_DICT.put("xun", "讯训迅孙寻询循旬巡汛勋逊熏徇浚殉驯鲟薰荀浔洵峋埙巽郇醺恂荨窨蕈曛獯");
        PINYIN_DICT.put("huang", "黄荒煌皇凰慌晃潢谎惶簧璜恍幌湟蝗磺隍徨遑肓篁鳇蟥癀");
        PINYIN_DICT.put("nai", "能乃奶耐奈鼐萘氖柰佴艿");
        PINYIN_DICT.put("luan", "乱卵滦峦鸾栾銮挛孪脔娈");
        PINYIN_DICT.put("qie", "切且契窃茄砌锲怯伽惬妾趄挈郄箧慊");
        PINYIN_DICT.put("jian", "建间件见坚检健监减简艰践兼鉴键渐柬剑尖肩舰荐箭浅剪俭碱茧奸歼拣捡煎贱溅槛涧堑笺谏饯锏缄睑謇蹇腱菅翦戬毽笕犍硷鞯牮枧湔鲣囝裥踺搛缣鹣蒹谫僭戋趼楗");
        PINYIN_DICT.put("nan", "南难男楠喃囡赧腩囝蝻");
        PINYIN_DICT.put("qian", "前千钱签潜迁欠纤牵浅遣谦乾铅歉黔谴嵌倩钳茜虔堑钎骞阡掮钤扦芊犍荨仟芡悭缱佥愆褰凵肷岍搴箝慊椠");
        PINYIN_DICT.put("qiang", "强抢疆墙枪腔锵呛羌蔷襁羟跄樯戕嫱戗炝镪锖蜣");
        PINYIN_DICT.put("xiang", "向项相想乡象响香降像享箱羊祥湘详橡巷翔襄厢镶飨饷缃骧芗庠鲞葙蟓");
        PINYIN_DICT.put("jiao", "教交较校角觉叫脚缴胶轿郊焦骄浇椒礁佼蕉娇矫搅绞酵剿嚼饺窖跤蛟侥狡姣皎茭峤铰醮鲛湫徼鹪僬噍艽挢敫");
        PINYIN_DICT.put("zhuo", "着著缴桌卓捉琢灼浊酌拙茁涿镯淖啄濯焯倬擢斫棹诼浞禚");
        PINYIN_DICT.put("qiao", "桥乔侨巧悄敲俏壳雀瞧翘窍峭锹撬荞跷樵憔鞘橇峤诮谯愀鞒硗劁缲");
        PINYIN_DICT.put("xiao", "小效销消校晓笑肖削孝萧俏潇硝宵啸嚣霄淆哮筱逍姣箫骁枭哓绡蛸崤枵魈");
        PINYIN_DICT.put("si", "司四思斯食私死似丝饲寺肆撕泗伺嗣祀厮驷嘶锶俟巳蛳咝耜笥纟糸鸶缌澌姒汜厶兕");
        PINYIN_DICT.put("kai", "开凯慨岂楷恺揩锴铠忾垲剀锎蒈");
        PINYIN_DICT.put("jin", "进金今近仅紧尽津斤禁锦劲晋谨筋巾浸襟靳瑾烬缙钅矜觐堇馑荩噤廑妗槿赆衿卺");
        PINYIN_DICT.put("qin", "亲勤侵秦钦琴禽芹沁寝擒覃噙矜嗪揿溱芩衾廑锓吣檎螓");
        PINYIN_DICT.put("jing", "经京精境竞景警竟井惊径静劲敬净镜睛晶颈荆兢靖泾憬鲸茎腈菁胫阱旌粳靓痉箐儆迳婧肼刭弪獍");
        PINYIN_DICT.put("ying", "应营影英景迎映硬盈赢颖婴鹰荧莹樱瑛蝇萦莺颍膺缨瀛楹罂荥萤鹦滢蓥郢茔嘤璎嬴瘿媵撄潆");
        PINYIN_DICT.put("jiu", "就究九酒久救旧纠舅灸疚揪咎韭玖臼柩赳鸠鹫厩啾阄桕僦鬏");
        PINYIN_DICT.put("zui", "最罪嘴醉咀蕞觜");
        PINYIN_DICT.put("juan", "卷捐圈眷娟倦绢隽镌涓鹃鄄蠲狷锩桊");
        PINYIN_DICT.put("suan", "算酸蒜狻");
        PINYIN_DICT.put("yun", "员运云允孕蕴韵酝耘晕匀芸陨纭郧筠恽韫郓氲殒愠昀菀狁");
        PINYIN_DICT.put("qun", "群裙逡麇");
        PINYIN_DICT.put("ka", "卡喀咖咔咯佧胩");
        PINYIN_DICT.put("kang", "康抗扛慷炕亢糠伉钪闶");
        PINYIN_DICT.put("keng", "坑铿吭");
        PINYIN_DICT.put("kao", "考靠烤拷铐栲尻犒");
        PINYIN_DICT.put("ken", "肯垦恳啃龈裉");
        PINYIN_DICT.put("yin", "因引银印音饮阴隐姻殷淫尹荫吟瘾寅茵圻垠鄞湮蚓氤胤龈窨喑铟洇狺夤廴吲霪茚堙");
        PINYIN_DICT.put("kong", "空控孔恐倥崆箜");
        PINYIN_DICT.put("ku", "苦库哭酷裤枯窟挎骷堀绔刳喾");
        PINYIN_DICT.put("kua", "跨夸垮挎胯侉");
        PINYIN_DICT.put("kui", "亏奎愧魁馈溃匮葵窥盔逵睽馗聩喟夔篑岿喹揆隗傀暌跬蒉愦悝蝰");
        PINYIN_DICT.put("kuan", "款宽髋");
        PINYIN_DICT.put("kuang", "况矿框狂旷眶匡筐邝圹哐贶夼诳诓纩");
        PINYIN_DICT.put("que", "确却缺雀鹊阙瘸榷炔阕悫");
        PINYIN_DICT.put("kun", "困昆坤捆琨锟鲲醌髡悃阃");
        PINYIN_DICT.put("kuo", "扩括阔廓蛞");
        PINYIN_DICT.put("la", "拉落垃腊啦辣蜡喇剌旯砬邋瘌");
        PINYIN_DICT.put("lai", "来莱赖睐徕籁涞赉濑癞崃疠铼");
        PINYIN_DICT.put("lan", "兰览蓝篮栏岚烂滥缆揽澜拦懒榄斓婪阑褴罱啉谰镧漤");
        PINYIN_DICT.put("lin", "林临邻赁琳磷淋麟霖鳞凛拎遴蔺吝粼嶙躏廪檩啉辚膦瞵懔");
        PINYIN_DICT.put("lang", "浪朗郎廊狼琅榔螂阆锒莨啷蒗稂");
        PINYIN_DICT.put("liang", "量两粮良辆亮梁凉谅粱晾靓踉莨椋魉墚");
        PINYIN_DICT.put("lao", "老劳落络牢捞涝烙姥佬崂唠酪潦痨醪铑铹栳耢");
        PINYIN_DICT.put("mu", "目模木亩幕母牧莫穆姆墓慕牟牡募睦缪沐暮拇姥钼苜仫毪坶");
        PINYIN_DICT.put("le", "了乐勒肋叻鳓嘞仂泐");
        PINYIN_DICT.put("lei", "类累雷勒泪蕾垒磊擂镭肋羸耒儡嫘缧酹嘞诔檑");
        PINYIN_DICT.put("sui", "随岁虽碎尿隧遂髓穗绥隋邃睢祟濉燧谇眭荽");
        PINYIN_DICT.put("lie", "列烈劣裂猎冽咧趔洌鬣埒捩躐");
        PINYIN_DICT.put("leng", "冷愣棱楞塄");
        PINYIN_DICT.put("ling", "领令另零灵龄陵岭凌玲铃菱棱伶羚苓聆翎泠瓴囹绫呤棂蛉酃鲮柃");
        PINYIN_DICT.put("lia", "俩");
        PINYIN_DICT.put("liao", "了料疗辽廖聊寥缪僚燎缭撂撩嘹潦镣寮蓼獠钌尥鹩");
        PINYIN_DICT.put("liu", "流刘六留柳瘤硫溜碌浏榴琉馏遛鎏骝绺镏旒熘鹨锍");
        PINYIN_DICT.put("lun", "论轮伦仑纶沦抡囵");
        PINYIN_DICT.put("lv", "率律旅绿虑履吕铝屡氯缕滤侣驴榈闾偻褛捋膂稆");
        PINYIN_DICT.put("lou", "楼露漏陋娄搂篓喽镂偻瘘髅耧蝼嵝蒌");
        PINYIN_DICT.put("mao", "贸毛矛冒貌茂茅帽猫髦锚懋袤牦卯铆耄峁瑁蟊茆蝥旄泖昴瞀");
        PINYIN_DICT.put("long", "龙隆弄垄笼拢聋陇胧珑窿茏咙砻垅泷栊癃");
        PINYIN_DICT.put("nong", "农浓弄脓侬哝");
        PINYIN_DICT.put("shuang", "双爽霜孀泷");
        PINYIN_DICT.put("shu", "术书数属树输束述署朱熟殊蔬舒疏鼠淑叔暑枢墅俞曙抒竖蜀薯梳戍恕孰沭赎庶漱塾倏澍纾姝菽黍腧秫毹殳疋摅");
        PINYIN_DICT.put("shuai", "率衰帅摔甩蟀");
        PINYIN_DICT.put("lve", "略掠锊");
        PINYIN_DICT.put("ma", "么马吗摩麻码妈玛嘛骂抹蚂唛蟆犸杩");
        PINYIN_DICT.put("me", "么麽");
        PINYIN_DICT.put("mai", "买卖麦迈脉埋霾荬劢");
        PINYIN_DICT.put("man", "满慢曼漫埋蔓瞒蛮鳗馒幔谩螨熳缦镘颟墁鞔");
        PINYIN_DICT.put("mi", "米密秘迷弥蜜谜觅靡泌眯麋猕谧咪糜宓汨醚嘧弭脒冖幂祢縻蘼芈糸敉");
        PINYIN_DICT.put("men", "们门闷瞒汶扪焖懑鞔钔");
        PINYIN_DICT.put("mang", "忙盲茫芒氓莽蟒邙硭漭");
        PINYIN_DICT.put("meng", "蒙盟梦猛孟萌氓朦锰檬勐懵蟒蜢虻黾蠓艨甍艋瞢礞");
        PINYIN_DICT.put("miao", "苗秒妙描庙瞄缪渺淼藐缈邈鹋杪眇喵");
        PINYIN_DICT.put("mou", "某谋牟缪眸哞鍪蛑侔厶");
        PINYIN_DICT.put("miu", "缪谬");
        PINYIN_DICT.put("mei", "美没每煤梅媒枚妹眉魅霉昧媚玫酶镁湄寐莓袂楣糜嵋镅浼猸鹛");
        PINYIN_DICT.put("wen", "文问闻稳温纹吻蚊雯紊瘟汶韫刎璺玟阌");
        PINYIN_DICT.put("mie", "灭蔑篾乜咩蠛");
        PINYIN_DICT.put("ming", "明名命鸣铭冥茗溟酩瞑螟暝");
        PINYIN_DICT.put("na", "内南那纳拿哪娜钠呐捺衲镎肭");
        PINYIN_DICT.put("nei", "内那哪馁");
        PINYIN_DICT.put("nuo", "难诺挪娜糯懦傩喏搦锘");
        PINYIN_DICT.put("ruo", "若弱偌箬");
        PINYIN_DICT.put("nang", "囊馕囔曩攮");
        PINYIN_DICT.put("nao", "脑闹恼挠瑙淖孬垴铙桡呶硇猱蛲");
        PINYIN_DICT.put("ni", "你尼呢泥疑拟逆倪妮腻匿霓溺旎昵坭铌鲵伲怩睨猊");
        PINYIN_DICT.put("nen", "嫩恁");
        PINYIN_DICT.put("neng", "能");
        PINYIN_DICT.put("nin", "您恁");
        PINYIN_DICT.put("niao", "鸟尿溺袅脲茑嬲");
        PINYIN_DICT.put("nie", "摄聂捏涅镍孽捻蘖啮蹑嗫臬镊颞乜陧");
        PINYIN_DICT.put("niang", "娘酿");
        PINYIN_DICT.put("ning", "宁凝拧泞柠咛狞佞聍甯");
        PINYIN_DICT.put("nu", "努怒奴弩驽帑孥胬");
        PINYIN_DICT.put("nv", "女钕衄恧");
        PINYIN_DICT.put("ru", "入如女乳儒辱汝茹褥孺濡蠕嚅缛溽铷洳薷襦蓐");
        PINYIN_DICT.put("nuan", "暖");
        PINYIN_DICT.put("nve", "虐疟");
        PINYIN_DICT.put("re", "热若惹喏");
        PINYIN_DICT.put("ou", "区欧偶殴呕禺藕讴鸥瓯沤耦怄");
        PINYIN_DICT.put("pao", "跑炮泡抛刨袍咆疱庖狍匏脬");
        PINYIN_DICT.put("pou", "剖掊裒");
        PINYIN_DICT.put("pen", "喷盆湓");
        PINYIN_DICT.put("pie", "瞥撇苤氕丿");
        PINYIN_DICT.put("pin", "品贫聘频拼拚颦姘嫔榀牝");
        PINYIN_DICT.put("se", "色塞瑟涩啬穑铯槭");
        PINYIN_DICT.put("qing", "情青清请亲轻庆倾顷卿晴氢擎氰罄磬蜻箐鲭綮苘黥圊檠謦");
        PINYIN_DICT.put("zan", "赞暂攒堑昝簪糌瓒錾趱拶");
        PINYIN_DICT.put("shao", "少绍召烧稍邵哨韶捎勺梢鞘芍苕劭艄筲杓潲");
        PINYIN_DICT.put("sao", "扫骚嫂梢缫搔瘙臊埽缲鳋");
        PINYIN_DICT.put("sha", "沙厦杀纱砂啥莎刹杉傻煞鲨霎嗄痧裟挲铩唼歃");
        PINYIN_DICT.put("xuan", "县选宣券旋悬轩喧玄绚渲璇炫萱癣漩眩暄煊铉楦泫谖痃碹揎镟儇");
        PINYIN_DICT.put("ran", "然染燃冉苒髯蚺");
        PINYIN_DICT.put("rang", "让壤攘嚷瓤穰禳");
        PINYIN_DICT.put("rao", "绕扰饶娆桡荛");
        PINYIN_DICT.put("reng", "仍扔");
        PINYIN_DICT.put("ri", "日");
        PINYIN_DICT.put("rou", "肉柔揉糅鞣蹂");
        PINYIN_DICT.put("ruan", "软阮朊");
        PINYIN_DICT.put("run", "润闰");
        PINYIN_DICT.put("sa", "萨洒撒飒卅仨脎");
        PINYIN_DICT.put("suo", "所些索缩锁莎梭琐嗦唆唢娑蓑羧挲桫嗍睃");
        PINYIN_DICT.put("sai", "思赛塞腮噻鳃");
        PINYIN_DICT.put("shui", "说水税谁睡氵");
        PINYIN_DICT.put("sang", "桑丧嗓搡颡磉");
        PINYIN_DICT.put("sen", "森");
        PINYIN_DICT.put("seng", "僧");
        PINYIN_DICT.put("shai", "筛晒");
        PINYIN_DICT.put("shang", "上商尚伤赏汤裳墒晌垧觞殇熵绱");
        PINYIN_DICT.put("xing", "行省星腥猩惺兴刑型形邢饧醒幸杏性姓陉荇荥擤悻硎");
        PINYIN_DICT.put("shou", "收手受首售授守寿瘦兽狩绶艏扌");
        PINYIN_DICT.put("shuo", "说数硕烁朔铄妁槊蒴搠");
        PINYIN_DICT.put("su", "速素苏诉缩塑肃俗宿粟溯酥夙愫簌稣僳谡涑蔌嗉觫");
        PINYIN_DICT.put("shua", "刷耍唰");
        PINYIN_DICT.put("shuan", "栓拴涮闩");
        PINYIN_DICT.put("shun", "顺瞬舜吮");
        PINYIN_DICT.put("song", "送松宋讼颂耸诵嵩淞怂悚崧凇忪竦菘");
        PINYIN_DICT.put("sou", "艘搜擞嗽嗖叟馊薮飕嗾溲锼螋瞍");
        PINYIN_DICT.put("sun", "损孙笋荪榫隼狲飧");
        PINYIN_DICT.put("teng", "腾疼藤滕誊");
        PINYIN_DICT.put("tie", "铁贴帖餮萜");
        PINYIN_DICT.put("tu", "土突图途徒涂吐屠兔秃凸荼钍菟堍酴");
        PINYIN_DICT.put("wai", "外歪崴");
        PINYIN_DICT.put("wang", "王望往网忘亡旺汪枉妄惘罔辋魍");
        PINYIN_DICT.put("weng", "翁嗡瓮蓊蕹");
        PINYIN_DICT.put("zhua", "抓挝爪");
        PINYIN_DICT.put("yang", "样养央阳洋扬杨羊详氧仰秧痒漾疡泱殃恙鸯徉佯怏炀烊鞅蛘");
        PINYIN_DICT.put("xiong", "雄兄熊胸凶匈汹芎");
        PINYIN_DICT.put("yo", "哟唷");
        PINYIN_DICT.put("yong", "用永拥勇涌泳庸俑踊佣咏雍甬镛臃邕蛹恿慵壅痈鳙墉饔喁");
        PINYIN_DICT.put("za", "杂扎咱砸咋匝咂拶");
        PINYIN_DICT.put("zai", "在再灾载栽仔宰哉崽甾");
        PINYIN_DICT.put("zao", "造早遭枣噪灶燥糟凿躁藻皂澡蚤唣");
        PINYIN_DICT.put("zei", "贼");
        PINYIN_DICT.put("zen", "怎谮");
        PINYIN_DICT.put("zeng", "增曾综赠憎锃甑罾缯");
        PINYIN_DICT.put("zhei", "这");
        PINYIN_DICT.put("zou", "走邹奏揍诹驺陬楱鄹鲰");
        PINYIN_DICT.put("zhuai", "转拽");
        PINYIN_DICT.put("zun", "尊遵鳟樽撙");
        PINYIN_DICT.put("dia", "嗲");
        PINYIN_DICT.put("nou", "耨");
    }

    /**
     * 根据拼音获取对应的汉字列表
     */
    public static List<String> getChineseByPinyin(String pinyin) {
        List<String> result = new ArrayList<>();

        // 首先尝试从百度词库中获取（返回的是词列表）
        List<String> baiduList = BaiduDictHelper.getChineseByPinyin(pinyin);
        if (baiduList != null && !baiduList.isEmpty()) {
            // 直接加入所有候选词
            result.addAll(baiduList);
        } else {
            // 如果百度词库中没有，则使用内置拼音字典（单字列表）
            String chineseChars = PINYIN_DICT.get(pinyin);
            if (chineseChars != null) {
                for (char c : chineseChars.toCharArray()) {
                    result.add(String.valueOf(c));
                }
            }
        }

        return result;
    }

    

    /**
     * 获取所有匹配的拼音前缀
     * 优化：仅从内置字典查找前缀匹配，避免遍历大词库
     */
    public static List<String> getMatchingPinyins(String input) {
        List<String> matches = new ArrayList<>();
        
        // 添加内置拼音字典中的匹配项
        for (String pinyin : PINYIN_DICT.keySet()) {
            if (pinyin.startsWith(input)) {
                if (!matches.contains(pinyin)) { // 避免重复
                    matches.add(pinyin);
                }
            }
        }
        
        return matches;
    }

    

    /**
     * 智能匹配输入的拼音，返回候选词
     */
    public static List<String> getCandidates(String input) {
        List<String> candidates = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return candidates;
        }

        // 1. 分词
        List<String> segments = segmentPinyin(input);
        
        // 2. 尝试从词库中精确匹配（将分词结果用'连接）
        if (!segments.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < segments.size(); i++) {
                if (i > 0) sb.append("'");
                sb.append(segments.get(i));
            }
            String key = sb.toString();
            
            // 查找完全匹配（可能返回多个词）
            List<String> exactMatches = BaiduDictHelper.getChineseByPinyin(key);
            if (exactMatches != null && !exactMatches.isEmpty()) {
                // 将所有匹配词加入候选列表
                candidates.addAll(exactMatches);
            }
        }
        
        // 3. 根据输入前缀，收集所有匹配的拼音候选字
        List<String> matchingPinyins = getMatchingPinyins(input);
        for (String pinyin : matchingPinyins) {
            List<String> chars = getChineseByPinyin(pinyin);
            for (String ch : chars) {
                if (!containsCandidate(candidates, ch)) {
                    candidates.add(ch);
                }
            }
        }

        // 4. 显示第一个分词的候选字（供逐字选择），确保不会重复
        if (!segments.isEmpty()) {
            String firstSegment = segments.get(0);
            List<String> firstChars = getChineseByPinyin(firstSegment);
            for (String s : firstChars) {
                if (!containsCandidate(candidates, s)) {
                    candidates.add(s);
                }
            }
        } else {
            // 如果分词失败，尝试作为单字处理
            List<String> rawMatches = getChineseByPinyin(input);
            for (String s : rawMatches) {
                if (!containsCandidate(candidates, s)) {
                    candidates.add(s);
                }
            }
        }
        
        return candidates;
    }

    /**
     * 检查候选列表是否已包含指定字符串
     */
    private static boolean containsCandidate(List<String> candidates, String str) {
        for (String candidate : candidates) {
            if (candidate.contains(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查列表中是否包含指定字符串
     */
    private static boolean containsAny(List<String> list, String str) {
        for (String item : list) {
            if (item.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 分词输入的拼音串（智能分词算法）
     */
    public static List<String> segmentPinyin(String input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return result;
        }

        int len = input.length();
        int pos = 0;

        // 优先匹配长的拼音（贪婪匹配算法）
        while (pos < len) {
            String matchedPinyin = null;
            // 从最长可能的拼音开始尝试匹配（拼音最长不超过6个字符）
            for (int i = Math.min(len, pos + 6); i > pos; i--) {
                String sub = input.substring(pos, i);
                if (BaiduDictHelper.containsPinyin(sub) || PINYIN_DICT.containsKey(sub)) {
                    matchedPinyin = sub;
                    break;
                }
            }

            if (matchedPinyin != null) {
                result.add(matchedPinyin);
                pos += matchedPinyin.length();
            } else {
                // 如果没有匹配到，尝试匹配单个字符
          String singleChar = input.substring(pos, pos + 1);
                if (PINYIN_DICT.containsKey(singleChar)) {
                    result.add(singleChar);
                    pos++;
                } else {
                    // 如果连单个字符都不是有效拼音，也添加进结果，保证显示完整
                    result.add(singleChar);
                    pos++;
                }
            }
        }

        return result;
    }

    /**
     * 根据声母进行分词（优先以声母进行划分）
     */
    public static List<String> segmentPinyinByConsonant(String input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return result;
        }

        // 特殊处理：对于yuan这样的词，提供可选分词
        if ("yuan".equals(input)) {
            // 优先返回完整词，其次是分词
            result.add("yuan");
            result.add("yu");
            result.add("an");
            return result;
        }

        // 按声母进行分词
        int len = input.length();
        int pos = 0;

        while (pos < len) {
            String matchedPinyin = null;
            // 从当前位置尝试匹配最长的有效拼音
            for (int i = Math.min(len, pos + 6); i > pos; i--) {
                String sub = input.substring(pos, i);
                if (BaiduDictHelper.containsPinyin(sub) || PINYIN_DICT.containsKey(sub)) {
                    matchedPinyin = sub;
                    break;
                }
            }

            if (matchedPinyin != null) {
                result.add(matchedPinyin);
                pos += matchedPinyin.length();
            } else {
                // 如果没有完整匹配，尝试按照声母规则分词
                String segment = segmentByConsonantRule(input.substring(pos));
                if (segment != null && !segment.isEmpty()) {
                    result.add(segment);
                    pos += segment.length();
                } else {
                    // 如果还是无法分词，按单个字符处理
                    pos++;
                }
            }
        }

        return result;
    }

    /**
     * 根据声母规则进行分词
     */
    private static String segmentByConsonantRule(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // 声母列表
        String[] consonants = {"b", "p", "m", "f", "d", "t", "n", "l", "g", "k", "h", "j", "q", "x", "zh", "ch", "sh", "r", "z", "c", "s", "y", "w"};

        // 从最长的声母开始尝试匹配
        for (int i = consonants.length - 1; i >= 0; i--) {
            String consonant = consonants[i];
            if (input.startsWith(consonant)) {
                // 检查这个声母后面的部分是否构成有效拼音
                String remaining = input.substring(consonant.length());
                if (remaining.length() > 0) {
                    // 尝试匹配声母+韵母的组合
                    for (int j = 1; j <= remaining.length(); j++) {
                        String vowel = remaining.substring(0, j);
                        String fullPinyin = consonant + vowel;
                        if (BaiduDictHelper.containsPinyin(fullPinyin) || PINYIN_DICT.containsKey(fullPinyin)) {
                            return fullPinyin;
                        }
                    }
                    // 如果声母+部分韵母无法构成有效拼音，则返回声母
                    return consonant;
                } else {
                    // 如果声母后面没有字符，则返回声母
                    return consonant;
                }
            }
        }

        // 如果不以声母开头，则尝试匹配韵母
        for (String pinyin : PINYIN_DICT.keySet()) {
            if (input.startsWith(pinyin)) {
                return pinyin;
            }
        }

        // 如果没有匹配到任何拼音，返回第一个字符
        return input.substring(0, 1);
    }

    /**
     * 获取所有可能的分词方案
     */
    public static List<List<String>> getAllSegmentationOptions(String input) {
        List<List<String>> options = new ArrayList<>();

        // 添加默认的贪婪匹配结果
        List<String> defaultSegmentation = segmentPinyin(input);
        if (!defaultSegmentation.isEmpty()) {
            options.add(defaultSegmentation);
        }

        // 添加按声母分词的结果
        List<String> consonantSegmentation = segmentPinyinByConsonant(input);
        if (!consonantSegmentation.isEmpty() && !consonantSegmentation.equals(defaultSegmentation)) {
            options.add(consonantSegmentation);
        }

        // 对于yuan这样的特殊情况，提供"yu'an"的分词选项
        if ("yuan".equals(input)) {
            List<String> yuAnOption = new ArrayList<>();
            yuAnOption.add("yu");
            yuAnOption.add("an");
            options.add(yuAnOption);
        }

        // 对于hunan这样的词，提供多种分词选项
        if ("hunan".equals(input)) {
            List<String> huNanOption = new ArrayList<>();
            huNanOption.add("hu");
            huNanOption.add("nan");
            options.add(huNanOption);
            
            List<String> hunAnOption = new ArrayList<>();
            hunAnOption.add("hun");
            hunAnOption.add("an");
            options.add(hunAnOption);
        }

        return options;
    }

    /**
     * 解析输入的拼音串，尝试分词
     */
    public static List<String> parsePinyin(String input) {
        return segmentPinyin(input);
    }
}
