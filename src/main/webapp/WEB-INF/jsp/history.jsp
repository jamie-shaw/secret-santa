<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="/images/SantaPresents2.gif" alt="Santa with Presents" />
</aside>

<section id="form-container">

    <form id="mainForm" action="/history" method="get">
        Choose a year: 
        <select name="selectedYear" id="selectedYear" onchange="updateAction();">
            <c:forEach var="year" items="${YEARS}">
                <option value="${year}" ${year eq SELECTED_YEAR ? 'selected' : ''} >${year}</option>
            </c:forEach>
        </select>
    </form>

    <table>
        <caption><b>${SELECTED_YEAR}</b></caption>
        <tr>
            <th>Santa</th>
            <th>Recipient</th>
        </tr>

        <c:forEach var="recipient" items="${RECIPIENTS}">
            <tr>
                <td>
                    ${recipient.userName}
                </td>
                <td>
                    ${recipient.recipient}
                </td>
            </tr>
        </c:forEach>
    </table>
   
    <a href="/home" class="button">Back to Santa Home</a>

</section>

<script>
    function updateAction() {
        mainForm.action = "/history/" + selectedYear.value;
        mainForm.submit();
    }
</script>
