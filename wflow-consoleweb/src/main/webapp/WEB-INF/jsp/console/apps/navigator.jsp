<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>

<style>
    #main #main-body {
        padding: 0px;
    }
</style>
<div id="nv" style="min-width:<c:out value="${100 + (32 * fn:length(builders))}" />%;">
    <div id="nv-form" class="nv-col nv-border">
        <button href="#" onclick="navCreate('form')" class="nv-button"><fmt:message key="console.form.create.label"/></button>
        <h4><fmt:message key="console.header.submenu.label.forms"/></h4>
        <ul class="nv-list">
            <c:forEach items="${formDefinitionList}" var="formDef">
                <li data-id="${formDef.id}">
                    <a class="nv-link" href="${pageContext.request.contextPath}/web/console/app/${appDef.id}/${appDef.version}/form/builder/${formDef.id}" target="_blank" title='<fmt:message key="console.form.common.label.id"/>: ${formDef.id};&#13;<fmt:message key="console.form.common.label.dateCreated"/>: <ui:dateToString date="${formDef.dateCreated}"/>;&#13;<fmt:message key="console.form.common.label.dateModified"/>: <ui:dateToString date="${formDef.dateModified}"/>;&#13;<fmt:message key="form.form.description"/>: <c:out value="${formDef.description}"/>'><button href="#" onclick="return checkUsageDelete('${formDef.id}', 'form', event)" class="nv-delete" title='<fmt:message key="general.method.label.delete"/>'><i class="fas fa-times"></i></button><span class="nv-link-name"><i class="fas fa-file-alt"></i> <c:out value="${formDef.name}"/></span> <span class="nv-form-table">${formDef.tableName}</span>
                        <div class="nv-extra" style="display:none"><div class="nv-subinfo"><c:out value="${formDef.description}"/></div></div>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </div>
    <div id="nv-list" class="nv-col nv-border">
        <button href="#" onclick="navCreate('datalist')" class="nv-button"><fmt:message key="console.datalist.create.label"/></button>
        <h4><fmt:message key="console.header.submenu.label.lists"/></h4>
        <ul class="nv-list">
            <c:forEach items="${datalistDefinitionList}" var="listDef">
                <li data-id="${listDef.id}">
                    <a class="nv-link" href="${pageContext.request.contextPath}/web/console/app/${appDef.id}/${appDef.version}/datalist/builder/${listDef.id}" target="_blank" title='<fmt:message key="console.datalist.common.label.id"/>: ${listDef.id};&#13;<fmt:message key="console.datalist.common.label.dateCreated"/>: <ui:dateToString date="${listDef.dateCreated}"/>;&#13;<fmt:message key="console.datalist.common.label.dateModified"/>: <ui:dateToString date="${listDef.dateModified}"/>;&#13;<fmt:message key="console.datalist.common.label.description"/>: <c:out value="${listDef.description}"/>'><button href="#" onclick="return checkUsageDelete('${listDef.id}', 'datalist', event)" class="nv-delete" title='<fmt:message key="general.method.label.delete"/>'><i class="fas fa-times"></i></button><span class="nv-link-name"><i class="fas fa-table"></i> <c:out value="${listDef.name}"/></span>
                        <div class="nv-extra" style="display:none"><div class="nv-subinfo"><c:out value="${listDef.description}"/></div></div>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </div>
    <div id="nv-userview" class="nv-col <c:if test="${fn:length(builders) > 0}"> nv-border</c:if>">
        <button href="#" onclick="navCreate('userview')" class="nv-button"><fmt:message key="console.userview.create.label"/></button>
        <h4><fmt:message key="console.header.submenu.label.userview"/></h4>
        <ul class="nv-list">
            <c:forEach items="${userviewDefinitionList}" var="userviewDef">
                <li data-id="${userviewDef.id}">
                    <a class="nv-link nv-left" href="${pageContext.request.contextPath}/web/console/app/${appDef.id}/${appDef.version}/userview/builder/${userviewDef.id}" target="_blank" title='<fmt:message key="console.userview.common.label.id"/>: ${userviewDef.id};&#13;<fmt:message key="console.userview.common.label.dateCreated"/>: <ui:dateToString date="${userviewDef.dateCreated}"/>;&#13;<fmt:message key="console.userview.common.label.dateModified"/>: <ui:dateToString date="${userviewDef.dateModified}"/>;&#13;<fmt:message key="console.userview.common.label.description"/>: <c:out value="${userviewDef.description}"/>'><button href="#" onclick="return checkUsageDelete('${userviewDef.id}', 'userview', event)" class="nv-delete" title='<fmt:message key="general.method.label.delete"/>'><i class="fas fa-times"></i></button><span class="nv-link-name"><i class="fas fa-desktop"></i> <c:out value="${userviewDef.name}"/></span>
                        <div class="nv-extra" style="display:none"><div class="nv-subinfo"><c:out value="${userviewDef.description}"/></div></div>
                    </a>
                    <c:if test="${appDef.published}">
                        <button class="nv-button-small" onclick="window.open('${pageContext.request.contextPath}/web/userview/${appDef.id}/${userviewDef.id}')" target="_blank"><fmt:message key="console.run.launch"/></button>
                    </c:if>
                </li>
            </c:forEach>
        </ul>
    </div>
    <div id="nv-clear"></div>
</div>

<script type="text/javascript">
    <ui:popupdialog var="builderwCreateDialog" src=""/>
    function navCreate(type){
        showCreateForm(type);
    }
    function showCreateForm(type){
        builderwCreateDialog.src = "${pageContext.request.contextPath}/web/console/app/${appDef.id}/${appDef.version}/" + type + "/create?builderMode=false";
        builderwCreateDialog.init();
    }
    function checkUsageDelete(id, type, event) {
        var messages = {
            'form': '<fmt:message key="console.form.delete.label.confirmation"/>',
            'datalist': '<fmt:message key="console.datalist.delete.label.confirmation"/>',
            'userview': '<fmt:message key="console.userview.delete.label.confirmation"/>',
        }
        if (confirm(messages[type])) {
            Usages.delete(id, type, {
                contextPath: '${pageContext.request.contextPath}',
                appId: '${appDef.id}',
                appVersion: '${appDef.version}',
                id: id,
                builder: type,
                confirmMessage: '<fmt:message key="dependency.usage.confirmDelete"/>',
                confirmLabel: '<fmt:message key="dependency.usage.confirmLabel"/>',
                cancelLabel: '<fmt:message key="dependency.usage.cencelLabel"/>'
            }, function () {
                window[type + 'Delete'](id, event);
            });
        }

        event.preventDefault();
        event.stopPropagation();
        return false;
    }
    function formDelete(selectedList, event) {
        UI.blockUI();
        Nav.deleteItem(selectedList, "form");
        var callback = {
            success: function () {
                UI.unblockUI();
                refreshNavigator();
            }
        }
        ConnectionManager.post('${pageContext.request.contextPath}/web/console/app/${appDef.id}/${appDef.version}/form/delete', callback, 'formId=' + selectedList);
    }
    function datalistDelete(selectedList, event) {
        UI.blockUI();
        Nav.deleteItem(selectedList, "list");
        var callback = {
            success: function () {
                UI.unblockUI();
                refreshNavigator();
            }
        }
        ConnectionManager.post('${pageContext.request.contextPath}/web/console/app/${appDef.id}/${appDef.version}/datalist/delete', callback, 'ids=' + selectedList);
    }
    function userviewDelete(selectedList, event) {
        UI.blockUI();
        Nav.deleteItem(selectedList, "userview");
        var callback = {
            success: function () {
                UI.unblockUI();
                refreshNavigator();
            }
        }
        ConnectionManager.post('${pageContext.request.contextPath}/web/console/app/${appDef.id}/${appDef.version}/userview/delete', callback, 'ids=' + selectedList);
    }
    $(function () {
        Nav.init($("#nv-container"), ${tagDef}, {
            contextPath : "${pageContext.request.contextPath}",
            buttons : "#nv-refresh",
            refreshBtn : "#refreshBtn",
            infoBtn : "#toggleInfo",
            search : "#nv-search",
            url : '${pageContext.request.contextPath}/web/console/app/${appDef.id}/${appDef.version}/navigator?hidden=true',
            tagUrl : '${pageContext.request.contextPath}/web/json/console/app/${appDef.id}/${appDef.version}/tagging',
            message : {
                'tags' : '<fmt:message key="console.tag.tags"/>',
                'createNew' : '<fmt:message key="console.tag.create"/>',
                'edit' : '<fmt:message key="console.tag.edit"/>',
                'save' : '<fmt:message key="general.method.label.save"/>',
                'create' : '<fmt:message key="general.method.label.create"/>',
                'delete' : '<fmt:message key="general.method.label.delete"/>',
                'name' : '<fmt:message key="console.tag.name"/>',
                'color' : '<fmt:message key="console.tag.color"/>',
                'search' : '<fmt:message key="console.tag.search"/>',
                'show' : '<fmt:message key="console.tag.show"/>',
                'hide' : '<fmt:message key="console.tag.hide"/>',
                'red' : '<fmt:message key="console.tag.red"/>',
                'pink' : '<fmt:message key="console.tag.pink"/>',
                'orange' : '<fmt:message key="console.tag.orange"/>',
                'yellow' : '<fmt:message key="console.tag.yellow"/>',
                'green' : '<fmt:message key="console.tag.green"/>',
                'lime' : '<fmt:message key="console.tag.lime"/>',
                'blue' : '<fmt:message key="console.tag.blue"/>',
                'sky' : '<fmt:message key="console.tag.sky"/>',
                'purple' : '<fmt:message key="console.tag.purple"/>',
                'black' : '<fmt:message key="console.tag.black"/>'
            }
        });
    });
</script>
