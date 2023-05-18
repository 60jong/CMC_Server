package com.sctk.cmc.service.dto.designer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FreshDesignerInfo implements FilteredDesignerInfo {
    private String name;
    private String profileImgUrl;
    private List<String> categoryNames;
}
