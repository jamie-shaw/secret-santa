<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="/images/SantaPointing2.gif" alt="Santa" />
</aside>
        
<section id="form-container">
    <form>
        <c:choose>
            <c:when test="${not empty RECIPIENT}">
          
                <h1>You're ${RECIPIENT}'s Secret Santa!</h1>
    
                <button formaction="/idea/summary">See Ideas from ${RECIPIENT}</button>
                <button formaction="/gift/summary">Give My Secret Santa Some Ideas</button>
                <button formaction="/email">Send a Message</button>    
                <button formaction="/pick/status">See Who's Picked a Recipient</button>
                <button formaction="/history">See Ghosts of Christmas Past</button>
    
    <%--             <% if CurrentUser = "Jamie" or CurrentUser = "jamie" then %> --%>
                    <button formaction="admin">Santa Admin</button>
    <%--             <% end if %> --%>
    
                <button formaction="login">Log Out</button>
    
                <br/><br/>
    <%--             <em>Only <%=DateDiff("d",  Now(), "12/25/" + YEAR)%> days 'til Santa comes!</em> --%>
                
            </c:when>
            <c:otherwise>
                <br/>
                <h1>You haven't chosen a recipient yet!</h1>
                <br/>  
                <button formaction="recipient/choose">Choose a recipient</button>
                <br/>
                <br/>
            </c:otherwise>
        </c:choose>

    </form>
</section>

