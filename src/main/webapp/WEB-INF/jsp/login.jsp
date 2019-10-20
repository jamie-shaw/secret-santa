<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="/images/SantaHead.gif" alt="Santa" />
</aside>

<section id="form-container">

    <form method="post" action="/login" onSubmit="return validate(this)">
        <c:if test="${ERROR ne null}" >
            <div class="alert alert-error">Your login failed.  Please check your username and password and try again.</div>
        </c:if>
        
        <label for="UserName">Enter your user name:</label>
        <input type="text" name="username" id="username"  size="15" autocomplete="off" />
        
        <label for="Password">Enter your password:</label>
        <input type="password" name="password" id="password" size="15" />

        <button type="submit">Log In To Secret Santa</button>

    </form>
</section>