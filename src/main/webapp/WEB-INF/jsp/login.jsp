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

        <fieldset>
            <legend>Edition</legend>
            <input type="radio" name="edition" value="shaw" /> Shaw&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="radio" name=edition value="fernald"/> Fernald<br/>
        </fieldset>
        
        <button type="submit">Log In To Secret Santa</button>

    </form>
</section>

<script>
    function validate(form) {
        if (form.username.value == '') {
            alert('Please enter a username.');
            return false;
        }
        
        if (form.password.value == '') {
            alert('Please enter a password.');
            return false;
        }
        
        if (form.edition.value == '') {
            alert('Please choose an edition.');
            return false;
        }

        return true;
    }
</script>