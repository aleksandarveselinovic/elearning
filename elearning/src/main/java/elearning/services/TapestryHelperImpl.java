package elearning.services;

import elearning.utils.CriteriaConfigurer;
import java.util.ArrayList;
import java.util.List;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.grid.ColumnSort;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

public class TapestryHelperImpl implements TapestryHelper {

    private Session session;

    public TapestryHelperImpl(Session session) {
        this.session = session;
    }

    @Override
    public GridDataSource getDataSource(final Class<?> entityType, final CriteriaConfigurer configurer) {
        return new GridDataSource() {

            private List<Object> results;

            private int startIndex;

            @Override
            public int getAvailableRows() {
                Criteria criteria = getCriteria();
                criteria.setProjection(Projections.rowCount());
                return (int) (long) (Long) criteria.uniqueResult();
            }

            private Criteria getCriteria() {
                Criteria criteria = session.createCriteria(entityType);
                configurer.configure(criteria);
                return criteria;
            }

            @Override
            public void prepare(int startIndex, int endIndex, List<SortConstraint> sortConstraints) {
                Criteria criteria = getCriteria();
                criteria.setFirstResult(startIndex);
                criteria.setMaxResults(endIndex - startIndex + 1);

                for (SortConstraint sortConstraint : sortConstraints) {
                    criteria.addOrder(sortConstraint.getColumnSort() == ColumnSort.ASCENDING ?
                        Order.asc(sortConstraint.getPropertyModel().getPropertyName()) :
                        Order.desc(sortConstraint.getPropertyModel().getPropertyName()));
                }

                this.startIndex = startIndex;
                results = criteria.list();
            }

            @Override
            public Object getRowValue(int i) {
                return results.get(i - startIndex);
            }

            @SuppressWarnings("rawtypes")
            @Override
            public Class getRowType() {
                return entityType;
            }
        };
    }

    @Override
    public <T> ValueEncoder<T> valueEncoder(final Class<T> entityType){
        return new ValueEncoder<T>() {
            @Override
            public String toClient(T t) {
                return String.valueOf(session.getIdentifier(t));
            }

            @Override
            public T toValue(String s) {
                return (T) session.get(entityType, Long.parseLong(s));
            }
        };
    }

    @Override
    public <T> SelectModel selectModel(final Class<T> entityType, final List<T> items,
        final LabelProvider<T> labelProvider) {
        return new AbstractSelectModel() {
            @Override
            public List<OptionGroupModel> getOptionGroups() {
                return null;
            }

            @Override
            public List<OptionModel> getOptions() {
                List<OptionModel> optionModels = new ArrayList<OptionModel>();
                for(T item: items){
                    optionModels.add(new OptionModelImpl(labelProvider.getLabel(item), item));
                }

                return optionModels;
            }
        };
    }

    public GridDataSource getDataSource(Class<?> entityClass) {
        return getDataSource(entityClass, new CriteriaConfigurer() {
            public void configure(Criteria criteria) {
                //Nothing to do.
            }
        });
    }
}
