package com.project.LaptechBE.models.submodels.submodelsOrder;

import com.project.LaptechBE.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusHistory {
    private String status;
    private Date date;
    private String note;
    @DBRef(lazy = true)
    private User updatedBy;
}
