<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<aside id="image-container">
    <img border="0" src="/images/SantaLaughing2.gif" alt="Santa Laughing" />
</aside>

<c:set var="action">
    <c:choose>
        <c:when test="${not empty giftId}">/gift/${GIFT.id}/update</c:when>
        <c:otherwise>/gift</c:otherwise>
    </c:choose>
</c:set>

<section id="form-container">
    
    <form action="${action}" method="post" onSubmit="return validate(this)">
        <p>
            <b>Please enter a description of your gift idea:</b>
            <textarea name="description" rows="2" cols="40" id="description" maxlength="250"><c:out value="${GIFT.description}"></c:out></textarea>
            <br/>
            <b>Please paste a link to your gift idea (optional):</b>
            <textarea name="link" rows="2" cols="40" id="link" maxlength="1000"><c:out value="${GIFT.link}"></c:out></textarea>
            <br/>
            <button>Save Idea</button>
        </p>
    </form>
    <p>
        <a class="button" href="/gift/summary">Cancel</a>
    </p>

</section>

<script>
    function validate(form) {
        if (form.Description.value == '') {
            alert('Please enter a description.');
            return false;
        }

        return true;
    }
</script>

