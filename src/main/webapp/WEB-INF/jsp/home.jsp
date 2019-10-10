<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="/images/SantaPointing2.gif" alt="Santa" />
</aside>
        
<section id="form-container">
    <form>
        <c:choose>
            <c:when test="${not empty RECIPIENT}">
          
                <h1>You're ${RECIPIENT}'s Secret Santa!</h1>
    
                <a class="button" href="idea/summary">See Ideas from ${RECIPIENT}</a>
                <a class="button" href="gift/summary">Give My Secret Santa Some Ideas</a>
                <a class="button" href="email">Send a Message</a>
                <a class="button" href="pick/status">See Who's Picked a Recipient</a>
                <a class="button" href="history">See Ghosts of Christmas Past</a>
    
    <%--             <% if CurrentUser = "Jamie" or CurrentUser = "jamie" then %> --%>
                     <a class="button" href="admin">Santa Admin</a>
    <%--             <% end if %> --%>
    
                <a class="button" href="login">Log Out</a>
    
                <br/><br/>
    <%--             <em>Only <%=DateDiff("d",  Now(), "12/25/" + YEAR)%> days 'til Santa comes!</em> --%>
                
            </c:when>
            <c:otherwise>
                <br/>
                <h1>You haven't chosen a recipient yet!</h1>
                <br/>  
                 <a class="button" href="recipient/choose">Choose a recipient</a>
                <br/>
                <br/>
            </c:otherwise>
        </c:choose>

    </form>
</section>

