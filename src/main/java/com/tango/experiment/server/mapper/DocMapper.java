package com.tango.experiment.server.mapper;

import com.tango.experiment.pojo.Doc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DocMapper {
    List<Doc> getAllDoc();

    int updateDoc(@Param("fileName") String fileName);
}
