<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<aside id="image-container">
    <img border="0" src="/images/SantaPointing2.gif" alt="Santa" />
</aside>

<section id="form-container">
    <c:choose>
        <c:when test="${not empty RECIPIENT.recipient}">
      
            <h1>You're ${RECIPIENT.recipient}'s Secret Santa!</h1>
            
            <a class="button" href="/idea/summary">See Ideas from ${RECIPIENT.recipient}</a>
            <a class="button" href="/gift/summary">Give My Secret Santa Some Ideas</a>
            <a class="button" href="/email">Send a Message</a>
            <a class="button" href="/pick/status">See Who's Picked a Recipient</a>
            <a class="button" href="/history/${systemContext.currentYear - 1}">See Ghosts of Christmas Past</a>
            <a class="button" href="/logout">Log Out</a>

            <br/>
            <strong>Only ${systemContext.daysUntilChristmas} days 'til Santa comes!</strong>
            
        </c:when>
        <c:otherwise>
            <br/>
            <h1>You haven't chosen a recipient yet!</h1>
            <br/>
            <form>
                <button class="button" formaction="/pick" formmethod="post">Choose a recipient</button>
            </form>
        </c:otherwise>
    </c:choose>

    <sec:authorize access="hasAnyRole('ADMIN')">
         <a class="button" href="admin">Santa Admin</a>
    </sec:authorize>

</section>

