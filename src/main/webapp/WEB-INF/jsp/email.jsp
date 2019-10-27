<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="images/SantaPointing2.gif" alt="Santa Pointing" />
</aside>
        
<section id="form-container">
    <form name="bodyform" method="post" onSubmit="return validate(this)">
        <fieldset>
            <legend>&nbsp;Send message to:&nbsp;</legend>
            <input type="radio" name="to" value="santa" checked/> My Secret Santa&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="radio" name="to" value="recipient"/> My Recipient <br/>
        </fieldset>

        Please enter your message below:
        <br/>
        <textarea name="message" rows="3" cols="40" maxlength="250"></textarea>
        <br/>
    
        <button formaction="/email">Send Message</button>
    </form>

    <a class="button" href="/home">Back to Santa Home</a>
</section>

<script>
    function validate(form) {
        if (form.Message.value == '') {
            alert('Please enter a message.');
            return false;
        }

        return true;
    }
</script>

