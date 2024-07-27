![License](https://img.shields.io/badge/License-MIT-ff4500)
![Stars](https://img.shields.io/github/stars/ehsan-habib-3h/Reddit)
<p align = "center">
  <img src = "./src/main/resources/lammerbutnoob/reddit/reddit-logo-small.png"/>
</p>

## 📚Introduction
🤓This repository was created for fun and as a hobby. It serves as a platform for practicing OOP in Java and honing my UI design skills using `JavaFX`. Specifically, I’m building a straightforward __offline__ clone of Reddit.
###### Reddit is an American social news aggregation, content rating, and forum social network. Registered users submit content to the site such as links, text posts, images, and videos, which are then voted up or down by other members.
## Overview
![Screenshot 2024-07-27 17-33-56](https://github.com/user-attachments/assets/a95550a5-6448-40f2-b49c-db6bc064b2bc)

### Aditional features:
* Blur on NSFW content(*spoiler disappears on click*)
* Can toggle if others can vote on your post
* All passwords as stored in the database in a hashed format
## Usage

After launching Reddit, you can:

- Sign up for a new account or log in with existing credentials.
- Use the search bar to find specific subreddits or content with ```u/``` or ```r/```.
- Post new content by clicking the 'New Post' button.
- Engage with the community by commenting and voting on posts.

## 🛠️Implementations
### Classes:
### Reddit
  1. Run the main method of Application
  2. Make stage and also switch between scenes
### Account
1. Managing the parameters like
   - Username
   - Email
   - Password
     
    and also **Delete Account❌**
2. Providing ```static``` functions for development like:
  - Get name/email by Id(optional, default: *currentUserId*)
  - Hash Password
  - Check hashed password
  - Login/Sign up
  - Update (username/email/password)

### Subview
1. Display the content posted in a specific subreddit
2. Retrieve Post(title/description/votes)
3. Delete a post
4. Commit comments
## 🤝Contributing
Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repository and create a pull request. You can also simply open an issue with the tag "enhancement".

Don't forget to give the project a star! Thanks again!
## 📜Acknowledgments

- [Reddit icons and color pallets](https://reddit.com)
- [Dr Saeed R Kheradpisheh(_Instructor_)](https://github.com/SRKH), [Farid Karimi(_TA_)](https://github.com/Farid-Karimi)
- Contributors and supporters
