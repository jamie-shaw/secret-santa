<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="/images/SantaLaughing2.gif" alt="Santa Laughing"/>
</aside>

<section id="form-container">
    <table class="bodytable">

        <caption>Ideas for My Santa</caption>
        
        <c:choose>
            <c:when test="${not empty GIFTS}">
                <c:forEach items="${GIFTS}" var="gift">
                    <tr>
                        <td width="100%">
                            <div class="wrap-all"><c:out value="${gift.description}"/></div>
                            <br/><br/>
                            <a href="${gift.link}" target="_blank">Link</a>
                        </td>
                        <td>
                            <form>
                                <button type="submit" formmethod="get" formaction="/gift/${gift.id}">Change</button>
                                <button type="submit" formmethod="post" formaction="/gift/${gift.id}/delete">Remove</button>
                            </form>
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

    <a class="button" href="/gift">
        <c:choose>
            <c:when test="${not empty gifts}">
                Add Another Idea
            </c:when>
            <c:otherwise>
                Add An Idea
            </c:otherwise>
        </c:choose>
    </a>

    <a class="button" href="/home">Back to Santa Home</a>

</section>
