**CHEST PROTECTION**

Just finished rewrite of this mod to allow for new features! These include Authorisation, config file, improved ui, and lots of small tweaks to improve the mod

Easy to use server side mod for fabric that implements locking chests as well as creating shops within them

**Locking**

Sign a book “LOCK”, put it in the first slot of a chest. It is now locked! Only you can open it. This protects against:
- explosions, eg. TNT, end crystals
- Wither break mechanics
- Placing chests next to it to move the book out of the first slot
- Breaking
- hoppers

**Selling**

Sign the book “SELL”, put it in the first slot of the chest. The same protections exist as locking. When you open the chest it will prompt you to put some items in the left and right hand middle slot. 

You can configure the amount of that item by clicking the paper next to it and inputting a count.

When you put items in some green wool will appear. These let you choose what data you want to keep with the item. For example buying swords, but you don’t care about its damage or enchantments or name. So click those pieces of wool so they turn red then click confirm. 

Another example is selling shulker. But you don’t care what colour box it is so click the green wool called Item so it turns red then close the chest. This now means that it will only match on the components selected and not the item type itself. This is important as it lets you sell containers of other items

**Authorisation**

If you right click a book you will be able to configure the shop or lock. You can search for players and then authorise them. This gives them the same privileges as the owner, eg. accessing profits or opening a locked chest.


Enjoy, any problems create a issue request on the GitHub page.

I’ll slowly update this project to newer versions when I have the time