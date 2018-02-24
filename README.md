# Download-File
download a file and show progress of that

download a file that can be zip, png , jpg ,... with your custom name or dynamic name that can be decalre in server side.

in this project i use ***[asp.net MVC](https://www.asp.net/mvc)*** for server side with this method
```
public ActionResult DownloadImage()
        {
            var dir = Server.MapPath("~/Images/Pictures/");
            var path = Path.Combine(dir, "imageName" + ".jpg"); //validate the path for security or use other means to generate the path.
            return base.File(path, "image/jpeg","imageName");
        }
```
and get it in app with ***[Retrofit](http://square.github.io/retrofit/)***
