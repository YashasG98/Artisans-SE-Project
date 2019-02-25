# Artisans-SE-Project
An android e-commerce app to facilitate minimization of loss to artisans due to middlemen.  

### Contributing
Please use branches with appropriate names while creating pull requests and refrain from making changes direcly in the master branch. Make sure you pull the latest changes before making any changes of your own.  
You can follow these steps to create a new branch:  
```
git pull upstream master
git checkout -b branch_name
//make necessary changes here
git push origin branch_name
//create the pull request to merge to upstream master
```
Once the pull request has been merged, you can delete the branch by
```
git checkout master
git pull upstream master
git branch -D branch_name
```
