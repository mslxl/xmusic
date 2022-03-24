importPackage(Packages.io.github.mslxl.xmusic.common.addon.entity)

const sourceID =  'io.github.mslxl.common.test.rhino'

const i18n = {
    'en': {
        'rst': 'Rhino Script Test',
        'test.song': 'Test'
    }
}

const song = {
    getDetail: (index)=>{
        return [new EntitySong(index,index.id, "name:" + index.id, "fake",null)]
    },
    getURL:()=>{},
    getOption:()=>{}
}

const collection = {
    collectionIter: ()=>{
        var elem = [
            new EntityCollectionIndex("11", sourceID),
            new EntityCollectionIndex("45", sourceID),
            new EntityCollectionIndex("14", sourceID)
        ];
        var obj = {};
        obj.idx = 0;
        obj.hasNext = function(){
            return obj.idx < elem.length;
        }
        obj.next = function(){
            return elem[obj.idx++];
        }
        return obj;
    },
    getDetail: (index)=>{
        return new EntityCollection(index, "name:"+ index.id, "Create in js", "Rhino script", null);
    },
    getContentIter:(index) => {
        var iter = {};
        iter.output = 0;
        iter.hasNext = function(){
            return iter.output < 100;
        }
        iter.next = function(){
            return new EntitySongIndex("test-" + iter.output, sourceID)
        }
        return iter;
    }
}
const explorer = {
    'test.song': {
        exploreIter: ()=>{
            var obj = {};

            obj.counter = 0;
            obj.hasNext = function(){
                return obj.counter < 50
            };
            obj.next = function(){
                return new EntitySongIndex("test-" + obj.counter, sourceID);
            };

            return obj;
        },
        exploreDetail: (index)=>{
            var obj = {};
      
            obj.output = true;
            obj.hasNext = function(){
                return obj.output;
            }
            obj.next = function(){
                obj.output = false;
                return new EntitySong(index, index.id, "name:" + index.id, "fake",null)
            }

            return obj;
        }
    }
}





export = {
    name: 'rst',
    id: sourceID,
    lang: i18n,
    processor: {
        song: song,
        collection: collection,
        explorer: explorer
    }
}
