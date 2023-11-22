<!DOCTYPE html>
<html>
<head>
  <title>Article List</title>
</head>
<body>
<h1>Article List</h1>
<ul>
  <#list articles as article>
    <li>${article.title} - ${article.commentCount} comments</li>
  </#list>
</ul>
</body>
</html>