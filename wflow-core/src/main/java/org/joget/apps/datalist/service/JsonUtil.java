package org.joget.apps.datalist.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.apps.app.service.AppUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListFilter;
import org.joget.apps.datalist.model.DataListAction;
import org.joget.apps.datalist.model.DataListBinder;
import org.joget.apps.datalist.model.DataListColumn;
import org.joget.apps.datalist.model.DataListColumnFormat;
import org.joget.apps.datalist.model.DataListFilterType;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.service.PropertyUtil;
import org.json.JSONArray;

/**
 * Utility class containing methods to convert to/from JSON
 */
public class JsonUtil {
    
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_LABEL = "label";
    public static final String PROPERTY_DESC = "description";
    public static final String PROPERTY_PAGE_SIZE = "pageSize";
    public static final String PROPERTY_CLASS_NAME = "className";
    public static final String PROPERTY_PROPERTIES = "properties";
    public static final String PROPERTY_ORDER = "order";
    public static final String PROPERTY_ORDER_BY = "orderBy";
    public static final String PROPERTY_BINDER = "binder";
    public static final String PROPERTY_COLUMNS = "columns";
    public static final String PROPERTY_SORTABLE = "sortable";
    public static final String PROPERTY_ROW_ACTIONS = "rowActions";
    public static final String PROPERTY_ACTION = "action";
    public static final String PROPERTY_ACTIONS = "actions";
    public static final String PROPERTY_FILTERS = "filters";
    public static final String PROPERTY_OPERATOR = "operator";
    public static final String PROPERTY_FILTER_TYPE = "type";
    public static final String PROPERTY_FORMAT = "format";

    /**
     * Convert from JSON string into an object. Specifically to support data list model classes.
     * @param <T>
     * @param json
     * @param classOfT
     * @return
     */
    public static <T extends Object> T fromJson(String json, Class<T> classOfT) {
        if (json == null) {
            return null;
        }

        // strip enclosing brackets
        json = json.trim();
        if (json.startsWith("(")) {
            json = json.substring(1);
        }
        if (json.endsWith(")")) {
            json = json.substring(0, json.length() - 1);
        }
        
        Object object = parseElementFromJson(json);
        return (T) object;
    }

    /**
     * Convert from an object into JSON. Specifically to support data list model classes.
     * @param obj
     * @return
     */
    public static Object parseElementFromJson(String json) {
        try {
            // create json object
            JSONObject obj = new JSONObject(json);

            // parse json object
            Object object = parseElementFromJsonObject(obj);
            
            return object;
        } catch (Exception ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static Object parseElementFromJsonObject(JSONObject obj) throws Exception {
        DataList object = (DataList) new DataList();
        if (object != null) {
            
            if (obj.has(PROPERTY_ID)) {
                object.setId(obj.getString(PROPERTY_ID));
            }
            if (obj.has(PROPERTY_NAME)) {
                object.setName(obj.getString(PROPERTY_NAME));
            }
            if (obj.has(PROPERTY_DESC)) {
                object.setDescription(obj.getString(PROPERTY_DESC));
            }
            if (obj.has(PROPERTY_PAGE_SIZE)) {
                object.setDefaultPageSize(obj.getInt(PROPERTY_PAGE_SIZE));
            }
            if (obj.has(PROPERTY_ORDER)) {
                object.setDefaultOrder(obj.getString(PROPERTY_ORDER));
            }
            if (obj.has(PROPERTY_ORDER_BY)) {
                object.setDefaultSortColumn(obj.getString(PROPERTY_ORDER_BY));
            }

            //set columns
            Collection<DataListColumn> columns = parseColumnsFromJsonObject(obj);
            DataListColumn[] temp = (DataListColumn[]) columns.toArray(new DataListColumn[columns.size()]);
            object.setColumns(temp);

            //set binder
            DataListBinder binder = parseBinderFromJsonObject(obj);
            object.setBinder(binder);

            //set actions
            Collection<DataListAction> actions = parseActionsFromJsonObject(obj);
            DataListAction[] temp2 = (DataListAction[]) actions.toArray(new DataListAction[actions.size()]);
            object.setActions(temp2);

            //set row actions
            Collection<DataListAction> rowActions = parseRowActionsFromJsonObject(obj);
            DataListAction[] temp3 = (DataListAction[]) rowActions.toArray(new DataListAction[rowActions.size()]);
            object.setRowActions(temp3);

            //set filters
            Collection<DataListFilter> filters = parseFiltersFromJsonObject(obj);
            DataListFilter[] temp4 = (DataListFilter[]) filters.toArray(new DataListFilter[filters.size()]);
            object.setFilters(temp4);
            
        }
        
        return object;
    }
    
    public static Collection<DataListFilter> parseFiltersFromJsonObject(JSONObject obj) throws JSONException, InstantiationException, IllegalAccessException {
        Collection<DataListFilter> property = new ArrayList<DataListFilter>();
        
        if (!obj.isNull(PROPERTY_FILTERS)) {
            JSONArray filters = obj.getJSONArray(PROPERTY_FILTERS);
            
            for (int i = 0; i < filters.length(); i++) {
                JSONObject filter = filters.getJSONObject(i);
                DataListFilter dataListFilter = new DataListFilter();
                
                if (filter.has(PROPERTY_NAME)) {
                    dataListFilter.setName(filter.getString(PROPERTY_NAME));
                }
                if (filter.has(PROPERTY_LABEL)) {
                    dataListFilter.setLabel(filter.getString(PROPERTY_LABEL));
                }
                if (filter.has(PROPERTY_OPERATOR)) {
                    dataListFilter.setOperator(filter.getString(PROPERTY_OPERATOR));
                }
                if (filter.has(PROPERTY_FILTER_TYPE)) {
                    DataListFilterType type = parseFilterTypeFromJsonObject(filter);
                    dataListFilter.setType(type);
                }
                
                property.add(dataListFilter);
            }
        }
        return property;
    }
    
    public static Collection<DataListAction> parseRowActionsFromJsonObject(JSONObject obj) throws JSONException, InstantiationException, IllegalAccessException {
        Collection<DataListAction> property = new ArrayList<DataListAction>();
        
        if (!obj.isNull(PROPERTY_ROW_ACTIONS)) {
            JSONArray actions = obj.getJSONArray(PROPERTY_ROW_ACTIONS);
            
            for (int i = 0; i < actions.length(); i++) {
                JSONObject action = actions.getJSONObject(i);
                if (action.has(PROPERTY_CLASS_NAME)) {
                    String className = action.getString(PROPERTY_CLASS_NAME);
                    DataListAction dataListAction = (DataListAction) loadPlugin(className);
                    if (dataListAction != null) {
                        dataListAction.setProperties(PropertyUtil.getPropertiesValueFromJson(action.getJSONObject(PROPERTY_PROPERTIES).toString()));
                        dataListAction.setProperty(PROPERTY_ID, action.getString(PROPERTY_ID));
                        property.add(dataListAction);
                    }
                }
            }
        }
        return property;
    }
    
    public static Collection<DataListAction> parseActionsFromJsonObject(JSONObject obj) throws JSONException, InstantiationException, IllegalAccessException {
        Collection<DataListAction> property = new ArrayList<DataListAction>();
        
        if (!obj.isNull(PROPERTY_ACTIONS)) {
            JSONArray actions = obj.getJSONArray(PROPERTY_ACTIONS);
            
            for (int i = 0; i < actions.length(); i++) {
                JSONObject action = actions.getJSONObject(i);
                if (action.has(PROPERTY_CLASS_NAME)) {
                    String className = action.getString(PROPERTY_CLASS_NAME);
                    DataListAction dataListAction = (DataListAction) loadPlugin(className);
                    if (dataListAction != null) {
                        dataListAction.setProperties(PropertyUtil.getPropertiesValueFromJson(action.getJSONObject(PROPERTY_PROPERTIES).toString()));
                        dataListAction.setProperty(PROPERTY_ID, action.getString(PROPERTY_ID));
                        property.add(dataListAction);
                    }
                }
            }
        }
        return property;
    }
    
    public static DataListBinder parseBinderFromJsonObject(JSONObject obj) throws JSONException, InstantiationException, IllegalAccessException {
        if (!obj.isNull(PROPERTY_BINDER)) {
            JSONObject binderObj = obj.getJSONObject(PROPERTY_BINDER);
            if (binderObj.has(PROPERTY_CLASS_NAME)) {
                String className = binderObj.getString(PROPERTY_CLASS_NAME);
                DataListBinder dataListBinder = (DataListBinder) loadPlugin(className);
                if (dataListBinder != null) {
                    dataListBinder.setProperties(PropertyUtil.getPropertiesValueFromJson(binderObj.getJSONObject(PROPERTY_PROPERTIES).toString()));
                    return dataListBinder;
                }
            }
        }
        return null;
    }
    
    public static DataListAction parseActionFromJsonObject(JSONObject obj) throws JSONException, InstantiationException, IllegalAccessException {
        try {
            if (!obj.isNull(PROPERTY_ACTION) && !"".equals(obj.getString(PROPERTY_ACTION))) {
                JSONObject actionObj = obj.getJSONObject(PROPERTY_ACTION);
                if (actionObj.has(PROPERTY_CLASS_NAME)) {
                    String className = actionObj.getString(PROPERTY_CLASS_NAME);
                    DataListAction dataListAction = (DataListAction) loadPlugin(className);
                    if (dataListAction != null) {
                        dataListAction.setProperties(PropertyUtil.getPropertiesValueFromJson(actionObj.getJSONObject(PROPERTY_PROPERTIES).toString()));
                        return dataListAction;
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.WARNING, "Invalid action for {0}", obj);
        }
        return null;
    }
    
    public static DataListColumnFormat parseFormatterFromJsonObject(JSONObject obj) throws JSONException, InstantiationException, IllegalAccessException {
        try {
            if (!obj.isNull(PROPERTY_FORMAT) && !"".equals(obj.getString(PROPERTY_FORMAT))) {
                JSONObject formatterObj = obj.getJSONObject(PROPERTY_FORMAT);
                if (formatterObj.has(PROPERTY_CLASS_NAME)) {
                    String className = formatterObj.getString(PROPERTY_CLASS_NAME);
                    DataListColumnFormat dataListColumnFormat = (DataListColumnFormat) loadPlugin(className);
                    if (dataListColumnFormat != null) {
                        dataListColumnFormat.setProperties(PropertyUtil.getPropertiesValueFromJson(formatterObj.getJSONObject(PROPERTY_PROPERTIES).toString()));
                        return dataListColumnFormat;
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.WARNING, "Invalid formater for {0}", obj);
        }
        return null;
    }
    
    public static Collection<DataListColumn> parseColumnsFromJsonObject(JSONObject obj) throws JSONException, InstantiationException, IllegalAccessException {
        Collection<DataListColumn> property = new ArrayList<DataListColumn>();
        
        if (!obj.isNull(PROPERTY_COLUMNS)) {
            JSONArray columns = obj.getJSONArray(PROPERTY_COLUMNS);
            
            for (int i = 0; i < columns.length(); i++) {
                JSONObject column = columns.getJSONObject(i);
                DataListColumn dataListColumn = new DataListColumn();
                
                if (column.has(PROPERTY_NAME) && !column.isNull(PROPERTY_NAME)) {
                    dataListColumn.setName(column.getString(PROPERTY_NAME));
                }
                if (column.has(PROPERTY_LABEL) && !column.isNull(PROPERTY_LABEL)) {
                    dataListColumn.setLabel(column.getString(PROPERTY_LABEL));
                }
                if (column.has(PROPERTY_SORTABLE) && !column.isNull(PROPERTY_SORTABLE)) {
                    dataListColumn.setSortable(column.getBoolean(PROPERTY_SORTABLE));
                }
                if (column.has(PROPERTY_ACTION) && !column.isNull(PROPERTY_ACTION)) {
                    DataListAction action = parseActionFromJsonObject(column);
                    dataListColumn.setAction(action);
                }
                if (column.has(PROPERTY_FORMAT) && !column.isNull(PROPERTY_FORMAT)) {
                    Collection<DataListColumnFormat> formatCollection = new ArrayList<DataListColumnFormat>();
                    DataListColumnFormat format = parseFormatterFromJsonObject(column);
                    formatCollection.add(format);
                    
                    dataListColumn.setFormats(formatCollection);
                }
                property.add(dataListColumn);
            }
        }
        return property;
    }
    
    public static DataListFilterType parseFilterTypeFromJsonObject(JSONObject obj) throws JSONException, InstantiationException, IllegalAccessException {
        if (!obj.isNull(PROPERTY_FILTER_TYPE)) {
            JSONObject filterTypeObj = obj.getJSONObject(PROPERTY_FILTER_TYPE);
            if (filterTypeObj.has(PROPERTY_CLASS_NAME)) {
                String className = filterTypeObj.getString(PROPERTY_CLASS_NAME);
                DataListFilterType dataListFilterType = (DataListFilterType) loadPlugin(className);
                if (dataListFilterType != null) {
                    dataListFilterType.setProperties(PropertyUtil.getPropertiesValueFromJson(filterTypeObj.getJSONObject(PROPERTY_PROPERTIES).toString()));
                    return dataListFilterType;                    
                }
            }
        }
        return null;
    }
    
    private static Plugin loadPlugin(String className) {
        Plugin plugin = null;
        if (className != null && !className.isEmpty()) {
            PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
            plugin = pluginManager.getPlugin(className);
        }
        return plugin;
    }
}
