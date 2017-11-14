<h1>Akka cluster + Spring boot</h1>

<p>Server run command</p>
<ul>
<li><b>First node:</b> gradle -PappPort=8080 -PakkaPort=2551 setPort bootRun</li>
<li><b>Second node:</b> gradle -PappPort=8081 -PakkaPort=2552 setPort bootRun</li>
</ul>
