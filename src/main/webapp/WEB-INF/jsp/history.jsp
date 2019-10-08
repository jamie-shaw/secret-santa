<!-- #include file = "components/setup.asp" -->
<!-- #include file = "components/authenticate.asp" -->
<!-- #include file = "components/header.asp" -->

<%
  'Declare local variables
  Dim recipients, recipient, recordNumber, i, gifts, selectedYear, years, currentYear

  'Get UserName from session
  CurrentUser = Session("CurrentUser")

  'Get all of the active years
  SQL = "SELECT distinct Year " + _
          "FROM recipient " + _
         "WHERE Year <> Year (Date()) " + _
          "ORDER BY Year DESC"

  Set years = Server.CreateObject("ADODB.Recordset")
  years.Source = SQL
  years.ActiveConnection = conn
  years.CursorType = 3 'adOpenStatic
  years.Open

  if years.RecordCount > 0 then
      'Get selected year from request.  If null, default to first in list
      selectedYear = Request.Form("selectedYear")

      if selectedYear = "" then
          selectedYear = CStr(years("Year"))
      end if

      'Open a recordset and select all recipients from the selected year
      SQL = "SELECT UserName, Recipient, Year " + _
            "FROM [recipient] " + _
            "WHERE Year = " + selectedYear + " " + _
            "ORDER BY Year DESC, UserName"

      Set users = Server.CreateObject("ADODB.Recordset")
      users.Source = SQL
      users.ActiveConnection = conn
      users.CursorType = 3 'adOpenStatic
      users.Open
  end if
%>

<aside id="image-container">
    <img border="0" src="images/SantaPresents2.gif" alt="Santa with Presents" />
</aside>
        
<section id="form-container">

    <% if years.RecordCount > 0 then %>

        <form id="mainForm" action="ui_History.asp" method="post">
            Choose a year: <select name="selectedYear" onchange="mainForm.submit();">
            <% 
                for i = 1 to years.RecordCount
                    currentYear = CStr(years("Year"))

				    Response.Write("<option value='" + currentYear + "'")
				    if currentYear = Cstr(selectedYear) then
					    Response.Write(" selected")
				    end if
				    Response.Write(">")
				    Response.Write(currentYear + "</option>")
				    if i = years.RecordCount then
					    Response.Write(vbCRLF)
				    end if

                    years.MoveNext
                next
            %>
            </select>
        </form>

        <table>
            <caption><b><%=selectedYear%></b></caption>
            <tr>
                <th>Santa</th>
                <th>Recipient</th>
            </tr>

            <% for i = 1 to users.RecordCount %>
                <tr>
                    <td>
                        <%=users("UserName")%>
                    </td>
                    <td>
                        <%=users("Recipient")%>
                    </td>
                </tr>
            <%
            users.MoveNext
            next
            %>
        </table>
    <% else %>
        <p>
            <br />
            Nothing to see here yet - check back next year!
            <br />
            <br />
            <br />
        <p/>
    <% end if %>

    <form>
        <button formaction="ui_SantaHome.asp">Back to Santa Home</button>
    </form>

</section>

<%
  'clean up
  years.close
  Set years = Nothing

  conn.close
  Set conn = Nothing
%>

<!-- #include file = "components/footer.asp" -->