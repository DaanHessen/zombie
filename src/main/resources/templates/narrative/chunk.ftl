<#if chunk.settlement??>
    <#if chunk.settlement.groundZero>
I'm navigating the ruins of ${chunk.settlement.name}. The air here is thick and unnatural. Spores drift lazily through the stagnant atmosphere. I have a terrible feeling about this place.
    <#else>
I'm walking through the remnants of ${chunk.settlement.name}. Before the collapse, this area housed ${chunk.settlement.preApocalypsePopulation} people. Now, nature is slowly reclaiming the <#if chunk.settlement.biome == "URBAN">concrete and asphalt<#else>abandoned structures</#if>.
    </#if>
<#else>
I'm out in the remote wilderness. There are no signs of human civilization here, but the rustling of the wind still keeps me on edge.
</#if>
