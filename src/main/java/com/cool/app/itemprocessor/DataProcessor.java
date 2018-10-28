package com.cool.app.itemprocessor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.cool.domain.DataDTO;

public class DataProcessor implements ItemProcessor<DataDTO, DataDTO> {
	
    private static final Logger log = LoggerFactory.getLogger(DataProcessor.class);
    
    @Override
    public DataDTO process(final DataDTO AnimeDTO) throws Exception {
    	
    	final String id = AnimeDTO.getId();
        final String title = AnimeDTO.getTitle();
        final String description = AnimeDTO.getDescription();

        final DataDTO transformedAnimeDTO = new DataDTO(id, title, description);

        log.info("Converting (" + AnimeDTO + ") into (" + transformedAnimeDTO + ")");

        return transformedAnimeDTO;
    }

}
