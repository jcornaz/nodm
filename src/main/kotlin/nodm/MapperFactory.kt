package nodm

interface MapperFactory {
    fun createMapper(databaseManager: DatabaseManager, entityManager: EntityManager, mappingFactory: MappingFactory): Mapper
}