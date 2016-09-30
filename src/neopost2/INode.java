package neopost2;

/**
 * Created by jhooba on 2016-09-18.
 */
public interface INode {
  String SRH_PATH = "http://rt.molit.go.kr/srh/";
  String SRH_DO = "srh.do";
  String GUGUN_DO = "getGugunListAjax.do";
  String DONG_DO = "getDongListAjax.do";
  String DANJI_DO = "getDanjiComboAjax.do";
  String MONTHLY_DO = "getMonthListAjax.do";

  void populate();
}
