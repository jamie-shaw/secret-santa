<aside id="image-container">
    <img border="0" src="images/SantaHead.gif" alt="Santa" />
</aside>

<section id="form-container">

    <form method="post" action="login/process"
        onSubmit="return validate(this)" id="loginform">
        <div class="alert alert-error"></div>


        <input type="hidden" name="action" value="login" /> <label
            for="UserName"> Enter your user name: </label> <input
            type="text" name="UserName" size="15" id="UserName"
            autocomplete="off" /> <label for="Password"> Enter
            your password: </label> <input type="password" name="Password"
            size="15" />

        <button type="submit">Log In To Secret Santa</button>

    </form>
</section>