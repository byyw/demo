package com.byyw.demo.po;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("gpsinfo")
public class Gpsinfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Double latitude;
    private Double longitude;
    // gcj02,火星坐标系(高德)
    private Double glat;
    private Double glng;
    // 地址，精确读：0.0001°
    private String addr;

}
