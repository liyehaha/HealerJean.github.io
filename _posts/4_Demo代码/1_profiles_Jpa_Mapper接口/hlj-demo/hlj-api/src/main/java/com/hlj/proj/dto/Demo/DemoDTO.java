package com.hlj.proj.dto.Demo;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.hlj.proj.common.group.ValidateGroup;
import com.hlj.proj.common.page.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author HealerJean
 * @version 1.0v
 * @ClassName DemoDTO
 * @date 2019/6/13  20:02.
 * @Description
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "demo实体类")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DemoDTO extends PageQuery {

    @ApiModelProperty(value = "demo 主键", hidden = true)
    private Long id;

    @ApiModelProperty(value = "姓名")
    @NotBlank(message = "姓名不能为空", groups = ValidateGroup.HealerJean.class)
    private String name;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "是否删除，10可用，99删除 ", hidden = true)
    private String delFlag;

    @ApiModelProperty(value = "创建人", hidden = true)
    private Long createUser;

    @ApiModelProperty(value = "创建人名字", hidden = true)
    private String createName;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private java.util.Date createTime;

    @ApiModelProperty(value = "更新人", hidden = true)
    private Long updateUser;

    @ApiModelProperty(value = "更新人名称", hidden = true)
    private String updateName;

    @ApiModelProperty(hidden = true)
    private java.util.Date updateTime;

}
