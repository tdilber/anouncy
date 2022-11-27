package com.beyt.anouncy.common.repository.elasticsearch;

import com.beyt.anouncy.common.entity.elasticsearch.AnnounceSearchItem;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.stream.Collectors;

public interface AnnounceSearchRepository extends ElasticsearchRepository<AnnounceSearchItem, String>, AnnounceSearchRepositoryInternal {
}

interface AnnounceSearchRepositoryInternal {
    Page<AnnounceSearchItem> search(String query, Pageable pageable);
}

class AnnounceSearchRepositoryInternalImpl implements AnnounceSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AnnounceSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<AnnounceSearchItem> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(QueryBuilders.queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<AnnounceSearchItem> hits = elasticsearchTemplate
                .search(nativeSearchQuery, AnnounceSearchItem.class)
                .map(SearchHit::getContent)
                .stream()
                .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
