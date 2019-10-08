<!-- #include file = "components/setup.asp" -->
<!-- #include file = "components/authenticate.asp" -->
<!-- #include file = "components/header.asp" -->

<%
  'Declare local variables
  Dim i
%>

<aside id="image-container">
    <img border="0" src="images/SantaPointing2.gif" alt="Santa Pointing" />
</aside>
        
<section id="form-container">

	<form action="process_ResetPassword.asp" method="post">
        <table>
            <caption>Password Reset</caption>

            <%
                SQL = "SELECT UserName " + _
                        "FROM [user]"

                Set users = Server.CreateObject("ADODB.Recordset")
                users.Source = SQL
                users.ActiveConnection = conn
                users.CursorType = 3 'adOpenStatic
                users.Open

                For i = 1 To users.RecordCount %>
                <tr>
                    <td>
                        <%=users("UserName")%>
                    </td>
                    <td>
                        <input type="checkbox" name="chk_<%=users("UserName")%>">
                    </td>
                </tr>
                <% users.MoveNext
                Next
                %>
            </table>

            <button type="submit">Reset Passwords</button>
    </form>

    <form>
        <button formaction="ui_Admin.asp">Back to Santa Admin</button>
    </form>

</section>

<%
    'clean up
    users.close
    Set users = Nothing
%>

<!-- #include file = "components/footer.asp" -->

