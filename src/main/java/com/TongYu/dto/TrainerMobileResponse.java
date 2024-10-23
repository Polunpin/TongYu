package com.TongYu.dto;

import com.TongYu.model.Trainer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询教练列表|移动端使用
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TrainerMobileResponse extends Trainer {

}
