package com.tango.experiment.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doc implements Serializable {
    @Serial
    private static final long serialVersionUID = -6853935012218807212L;
    private Integer documentId;
    private String fileName;
    private String description;
    private Timestamp uploadAt;
    private Integer downloadCount;
}
