package cn.yunyichina.log.common.entity.do_;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Leo
 * @Blog: http://blog.csdn.net/lc0817
 * @CreateTime: 2017/3/1 10:03
 * @Description:
 */
@Getter
@Setter
public class CollectedItemDO implements Serializable {
    private static final long serialVersionUID = 685370116673816671L;

    private Integer id;
    private String name;
    private Integer collectorId;
    private String stdoutFilePath;
    private String collectedLogDir;
    private String applicationName;

    //    extra
    private List<KeywordTagDO> keywordTagList;
    private List<KvTagDO> kvTagList;


}
