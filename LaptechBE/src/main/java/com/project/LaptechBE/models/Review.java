package com.project.LaptechBE.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document("reviews")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Review {
    @Id
    @Field("_id")
    @JsonProperty("_id")  // Giữ tên trường là _id trong JSON trả về
    @JsonSerialize(using = ToStringSerializer.class)  // Chuyển ObjectId thành chuỗi khi trả về
    private ObjectId id;

    @DBRef(lazy = true)
    private Product productId;

    @DBRef(lazy = true)
    private User userId;

    private int rating;

    private String comment;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
