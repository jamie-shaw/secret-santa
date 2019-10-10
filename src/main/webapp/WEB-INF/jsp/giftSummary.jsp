<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="/images/SantaLaughing2.gif" alt="Santa Laughing"/>
</aside>
        
<c:set var="gifts" value="#{GIFTS}"/>

<section id="form-container">
    <table class="bodytable">

        <caption>Ideas for My Santa</caption>
        
        <c:choose>
            <c:when test="${not empty gifts}">
                <c:forEach items="${gifts}" var="gift">
                    <tr>
                        <td width="100%">
                            <c:out value="${gift.description}"/>
                        </td>
                        <td>
                            <a class="button" href="/gift/detail?action=update&giftId=${gift.id}">Change</a>
                            <a class="button" href="/gift/delete?giftId=${gift.id}">Remove</a>
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

    <form>
        <button href="gift/detail?action=create">
            <c:choose>
                <c:when test="${not empty gifts}">
                    Add Another Idea
                </c:when>
                <c:otherwise>
                    Add An Idea
                </c:otherwise>
            </c:choose>
        </button>
    </form>

    <a class="button" href="/home">Back to Santa Home</a>

</section>
