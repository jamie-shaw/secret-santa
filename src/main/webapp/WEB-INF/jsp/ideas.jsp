<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="/images/SantaPointing2.gif" alt="Santa" />
</aside>

<section id="form-container">
    
    <h1>${RECIPIENT}'s Ideas for Santa</h1>
    
    <table>
        <c:choose>
            <c:when test="not empty IDEAS">
                <c:forEach var="idea" items="IDEAS">
                    <tr>
                        <td>
                            <c:out value="${ideas.Description}" />
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr>
                    <td class="align-center">
                        No suggestions have been made yet.
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
    </table>
    
    <a class="button" href="/home">Back to Santa Home</a>

</section>
