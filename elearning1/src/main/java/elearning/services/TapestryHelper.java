package elearning.services;

import elearning.utils.CriteriaConfigurer;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.grid.GridDataSource;

import java.util.List;

public interface TapestryHelper {

    /**
     * Creates {@link GridDataSource} for a given hibernate entity.
     *
     * @see {@link CriteriaConfigurer}
     */
    GridDataSource getDataSource(Class<?> entityType, CriteriaConfigurer configurer);

    /**
     * Creates a ValueEncoder for a given hibernate entity. It is assumed that the
     * primary key of the class is a {@link Long} identifier.
     */
    <T> ValueEncoder<T> valueEncoder(Class<T> entityType);

    /**
     * Creates a {@link SelectModel} for a given hibernate entity.
     */
    <T> SelectModel selectModel(Class<T> entityType, List<T> items, LabelProvider<T> labelProvider);

    /**
     * Creates {@link GridDataSource} to a given hibernate entity.
     *
     */
    GridDataSource getDataSource(Class<?> entityClass);

}
