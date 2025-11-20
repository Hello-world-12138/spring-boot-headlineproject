// src/main/java/com/amk/pojo/BrowseHistory.java
package com.amk.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("browse_history")
public class BrowseHistory {
    private Long id;
    private Integer uid;
    private Integer hid;
    private Date browseTime;
}