package com.beyt.anouncy.common.entity.elasticsearch;

import com.beyt.anouncy.common.entity.neo4j.enumeration.RegionStatus;
import com.beyt.anouncy.common.entity.neo4j.enumeration.RegionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "announce")
public class AnnounceSearchItem implements Serializable {
    @Id
    @NotNull
    private String id;

    @NotNull
    private String body;

    @NotNull
    private UUID anonymousUserId;

    @NotNull
    private RegionSearchItem beginRegion;

    @NotNull
    private Date createDate;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionSearchItem implements Serializable {

        @NotNull
        private String id;

        @NotNull
        private String name;

        private Integer ordinal;

        @NotNull
        private Double latitude;

        @NotNull
        private Double longitude;

        @NotNull
        private Long locationId;

        @NotNull
        private RegionType type;

        @NotNull
        private RegionStatus status;
    }
}