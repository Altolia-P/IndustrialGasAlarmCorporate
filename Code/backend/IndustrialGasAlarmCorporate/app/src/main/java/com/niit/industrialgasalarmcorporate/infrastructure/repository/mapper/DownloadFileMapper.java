package com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.DownloadFilePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DownloadFileMapper extends BaseMapper<DownloadFilePO> {
}
