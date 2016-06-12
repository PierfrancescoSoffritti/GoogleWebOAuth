# WebBasedOAuth

Google offers some native ways to implement OAuth authentication in Android apps, but all of them suffer of the same problem: they don't allow access to secondary account (or linked account) and force the user to log into his main account.
This is not a problem in most cases, but in other cases can be a huge problem.
For example: YouTube allows an account to have a main channel and multiple secondary channels. Some users may use regularly the secondary channels, so if you're building an app that lets them authenticate in their YouTube channel, you must provide the option to log into both the main channel and the secondary channels. Otherwise they won't use your app.

Apparently the only way to have this basic functionality is to use the web-based OAuth process.

This library implements the web-based OAuth process in a simple way, in order to solve the problem illustared before.

If you don't need to access any secondary account (some Google services can't even use them) you should use the libraries provided by Google.
