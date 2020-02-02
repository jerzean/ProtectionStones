/*
 * Copyright 2019 ProtectionStones team and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.espi.protectionstones.commands;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.espi.protectionstones.PSGroupRegion;
import dev.espi.protectionstones.PSL;
import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import dev.espi.protectionstones.utils.UUIDCache;
import dev.espi.protectionstones.utils.WGUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ArgCount implements PSCommandArg {

    // Only PS regions, not other regions
    static int[] countRegionsOfPlayer(UUID uuid, World w) {
        int[] count = {0, 0}; // total, including merged
        try {
            RegionManager rgm = WGUtils.getRegionManagerWithWorld(w);
            for (ProtectedRegion pr : rgm.getRegions().values()) {
                if (ProtectionStones.isPSRegion(pr)) {
                    PSRegion r = PSRegion.fromWGRegion(w, pr);

                    if (r.isOwner(uuid)) {
                        count[0]++;
                        if (r instanceof PSGroupRegion) {
                            count[1] += ((PSGroupRegion) r).getMergedRegions().size();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return count;
    }

    @Override
    public List<String> getNames() {
        return Collections.singletonList("count");
    }

    @Override
    public boolean allowNonPlayersToExecute() {
        return false;
    }

    @Override
    public List<String> getPermissionsToExecute() {
        return Arrays.asList("protectionstones.count", "protectionstones.count.others");
    }

    @Override
    public HashMap<String, Boolean> getRegisteredFlags() {
        return null;
    }

    // /ps count
    @Override
    public boolean executeArgument(CommandSender s, String[] args, HashMap<String, String> flags) {
        Player p = (Player) s;
        Bukkit.getScheduler().runTaskAsynchronously(ProtectionStones.getInstance(), () -> {
            int[] count;

            if (args.length == 1) {
                if (!p.hasPermission("protectionstones.count")) {
                    PSL.msg(p, PSL.NO_PERMISSION_COUNT.msg());
                    return;
                }

                count = countRegionsOfPlayer(p.getUniqueId(), p.getWorld());
                PSL.msg(p, PSL.PERSONAL_REGION_COUNT.msg().replace("%num%", "" + count[0]));
                if (count[1] != 0) {
                    PSL.msg(p, PSL.PERSONAL_REGION_COUNT_MERGED.msg().replace("%num%", ""+count[1]));
                }

            } else if (args.length == 2) {

                if (!p.hasPermission("protectionstones.count.others")) {
                    PSL.msg(p, PSL.NO_PERMISSION_COUNT_OTHERS.msg());
                    return;
                }
                if (!UUIDCache.nameToUUID.containsKey(args[1])) {
                    PSL.msg(p, PSL.PLAYER_NOT_FOUND.msg());
                    return;
                }

                count = countRegionsOfPlayer(UUIDCache.nameToUUID.get(args[1]), p.getWorld());

                PSL.msg(p, PSL.OTHER_REGION_COUNT.msg()
                        .replace("%player%", args[1])
                        .replace("%num%", "" + count[0]));
                if (count[1] != 0) {
                    PSL.msg(p, PSL.OTHER_REGION_COUNT_MERGED.msg()
                            .replace("%player%", args[1])
                            .replace("%num%", "" + count[1]));
                }
            } else {
                PSL.msg(p, PSL.COUNT_HELP.msg());
            }
        });
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return null;
    }

}
