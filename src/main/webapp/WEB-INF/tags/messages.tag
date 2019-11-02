<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<c:if test="${not empty SUCCESS_MESSAGE}">
    <div class="alert alert-success">
        ${SUCCESS_MESSAGE}
    </div>
    <c:remove var="SUCCESS_MESSAGE" scope="session" />
</c:if>

<c:if test="${not empty ERROR_MESSAGE}">
    <section id="message-container">
        <div class="alert alert-error">
            ${ERROR_MESSAGE}
        </div>
    </section>
    
    <c:remove var="ERROR_MESSAGE" scope="session" />
</c:if>