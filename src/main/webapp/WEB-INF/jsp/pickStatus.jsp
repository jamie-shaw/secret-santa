<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="/images/SantaRunning2.gif" alt="Santa Running" />
</aside>

<section id="form-container">

    <table>

        <caption>Secret Santa Pick Status</caption>

        <c:forEach var="picker" items="${PICKERS}">
            <tr>
                <td>
                    ${picker.userName}
                </td>
                <td>
                    <c:choose>
                        <c:when test="${not empty picker.recipient}">
                            <input type="checkbox" checked onclick="this.checked=true;"/>
                        </c:when>
                        <c:otherwise>
                            <input type="checkbox" onclick="this.checked=null">
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </table>

    <form>
        <a class="button" href="home">Back to Santa Home</a>
    </form>

</section>
