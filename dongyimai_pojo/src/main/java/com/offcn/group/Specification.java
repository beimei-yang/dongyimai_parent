package com.offcn.group;

import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

// 聚合类 包含了 规格的属性 和 规格选项（多个）
public class Specification implements Serializable {

    private TbSpecification  specification;
    private List<TbSpecificationOption> specificationOptionList;

    public Specification(TbSpecification specification, List<TbSpecificationOption> specificationOptionList) {
        this.specification = specification;
        this.specificationOptionList = specificationOptionList;
    }

    public Specification() {
    }

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
