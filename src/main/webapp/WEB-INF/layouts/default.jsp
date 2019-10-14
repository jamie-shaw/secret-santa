<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title><tiles:insertAttribute name="title" />Welcome to Secret Santa!</title>
    
      <link rel="stylesheet" href="/css/reset.css" type="text/css" media="screen" />
      <link rel="stylesheet" href="/css/styles.css" type="text/css" media="screen" />
    
      <script src="/js/modernizr.min.js"></script>
      <script src="/js/respond.min.js"></script>
      <script src="/js/prefixfree.min.js"></script>
      <script src="/js/main.js"></script>
    </head>

    <body onload="setFocus();">

        <header>
            <img border="0" src="/images/SecretSanta2018.gif" />
        </header>
    
        <div id="main-content" class="container clearfix">
            <tiles:insertAttribute name="content" />
        </div>

        <footer/>

    </body>

</html>