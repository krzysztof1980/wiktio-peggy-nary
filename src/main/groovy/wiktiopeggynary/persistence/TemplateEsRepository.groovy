package wiktiopeggynary.persistence

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.kwitukiewicz.wdb.elasticsearch.ElasticsearchIndexingRepository
import wiktiopeggynary.parser.template.model.TemplateDefinition

/**
 * @author Krzysztof Witukiewicz
 */
class TemplateEsRepository extends ElasticsearchIndexingRepository {
    private static final Logger logger = LoggerFactory.getLogger(TemplateEsRepository.class)

    private static final String TEMPLATE_DEF_INDEX = "dewiktionary_templates"
    private static final String TEMPLATE_DEF_TYPE = "template"

    TemplateEsRepository() {
        super(TEMPLATE_DEF_INDEX, TEMPLATE_DEF_TYPE)
    }

    void indexTemplateDefinition(TemplateDefinition templateDefinition) {
        indexObject(templateDefinition)
    }

    @Override
    protected Logger getLogger() {
        return logger
    }
}
