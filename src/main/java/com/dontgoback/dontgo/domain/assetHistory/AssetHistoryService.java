//package com.dontgoback.dontgo.domain.assetHistory;
//
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class AssetHistoryService {
//    private AssetHistoryRepository assetRepository;
//
//    public Optional<AssetHistory> getAsset(long id){
//        return assetRepository.findById(id);
//    }
//
//    public Optional<Long> getUserName(long id){
//        return getAsset(id).map(AssetHistory::getId);
//    }
//}
